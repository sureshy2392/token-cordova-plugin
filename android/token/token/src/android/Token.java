package com.at.token;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.token.DeviceInfo;
import io.token.proto.PagedList;
import io.token.proto.common.blob.BlobProtos;
import io.token.proto.common.member.MemberProtos;
import io.token.proto.common.notification.NotificationProtos;
import io.token.proto.common.security.SecurityProtos;
import io.token.proto.common.subscriber.SubscriberProtos;
import io.token.proto.common.transfer.TransferProtos;
import io.token.security.AKSCryptoEngineFactory;
import io.token.security.CryptoEngine;
import io.token.security.CryptoEngineFactory;
import io.token.security.UserAuthenticationStore;
import io.token.user.AccessTokenBuilder;
import io.token.user.Account;
import io.token.user.Member;
import io.token.user.TokenClient;
// import custom.TokenClient;

import io.token.proto.common.alias.AliasProtos;
// import io.reactivex.android.schedulers.AndroidSchedulers;
// import io.reactivex.functions.Action;
// import io.reactivex.functions.Consumer;
import android.app.KeyguardManager;
import android.content.Context;
import io.token.proto.common.token.TokenProtos;
import io.token.proto.common.alias.AliasProtos;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;


import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

// import okhttp3.Call;
// import okhttp3.Callback;
// import okhttp3.MediaType;
// import okhttp3.OkHttpClient;
// import okhttp3.Request;
// import okhttp3.RequestBody;
// import okhttp3.Response;

/**
 * This class echoes a string called from JavaScript.
 */

public class Token extends CordovaPlugin {

    private static final int LOCK_REQUEST_CODE = 221;
    private static final int SECURITY_SETTING_REQUEST_CODE = 233;

  public UserAuthenticationStore userAuthenticationStore;
    public CryptoEngineFactory cryptoEngineFactory;
    public CryptoEngine cryptoEngine;
    public TokenClient tokenClient;
    public AliasProtos.Alias alias,bankAlias;
    public String member_id ;
    public SecurityProtos.Key privilegedKey;
    NotificationProtos.AddKey addkey;
    public NotificationProtos.CreateAndEndorseToken content;
    public NotificationProtos.CreateAndEndorseToken.Builder builder;
    MemberProtos.MemberRecoveryOperation.Authorization authorization;
    public static final String developerKey = "4qY7lqQw8NOl9gng0ZHgT4xdiDqxqoGVutuZwrUYQsI";
    public static final String realm = "at-bisb";

    public String recoveryAgent = "m:4A6NpTk5XS3GuUEdjMZSTEWpjKD6:5zKtXEAq";
    public AliasProtos.Alias.Type type_user;
    public static final AliasProtos.Alias.Type type_bank = AliasProtos.Alias.Type.BANK;
    public static final io.token.TokenClient.TokenCluster cluster = io.token.TokenClient.TokenCluster.SANDBOX;
    Context context;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        context = this.cordova.getActivity().getApplicationContext();

        if (action.equals("createMember")) {
            this.createMember(args,callbackContext);
            return true;
        } else if(action.equals("linkAccounts")){
            this.linkAccounts(args,callbackContext);
            return true;
        } else if(action.equals("subscribe")){
            this.subscribe(args,callbackContext);
            return true;
        } else if(action.equals("getAccounts")){
            this.getAccounts(args,callbackContext);
            return true;
        } else if(action.equals("unlinkAccounts")){
            this.unlinkAccounts(args,callbackContext);
            return true;
        } else if(action.equals("deleteMember")){
            this.deleteMember(args,callbackContext);
            return true;
        } else if(action.equals("getTransfers")){
            this.getTransfers(args,callbackContext);
            return true;
        } else if(action.equals("getConsents")){
            this.getConsents(args,callbackContext);
            return true;
        } else if(action.equals("getProfile")){
            this.getProfile(args,callbackContext);
            return true;
        } else if(action.equals("getProfilePicture")){
            this.getProfilePicture(args,callbackContext);
            return true;
        } else if(action.equals("getAccount")){
            this.getAccount(args,callbackContext);
            return true;
        } else if(action.equals("approveAccessToken")){
            this.approveAccessToken(args,callbackContext);
            return true;
        } else if(action.equals("cancelAccessToken")){
            this.cancelAccessToken(args,callbackContext);
            return true;
        } else if(action.equals("approveTransferToken")){
            this.approveTransferToken(args,callbackContext);
            return true;
        } else if(action.equals("onAccountRevoke")){
            this.onAccountRevoke(args,callbackContext);
            return true;
        } else if(action.equals("memberRecovery")){
            this.memberRecovery(args,callbackContext);
            return true;
        } else if(action.equals("getRecoveredMember")){
            this.getRecoveredMember(args,callbackContext);
            return true;
        } else if(action.equals("provisionRequest")){
            this.provisionRequest(args,callbackContext);
            return true;
        } else if(action.equals("provisionResponse")){
            this.provisionResponse(args,callbackContext);
            return true;
        } else if(action.equals("getMember")){
            this.getMember(args,callbackContext);
            return true;
        } else if(action.equals("resolveAlias")){
            this.resolveAlias(args,callbackContext);
            return true;
        }


        return false;
    }

      private void createMember(JSONArray args,CallbackContext callbackContext) {
          JSONObject jsonObject;
          try {
             System.out.println("args======"+args.getString(0));
             //String mobileNumber = new JSONObject(args.getString(0)).getString("mobileNumber");
                String aliasValue = new JSONObject(args.getString(0)).getString("aliasValue");
                String aliasType = new JSONObject(args.getString(0)).getString("aliasType");
                tokenClient = getTokenClient(context);
                alias = getAlias(aliasType,aliasValue);
                bankAlias = getBankAlias();
                recoveryAgent = getBankMember();

        member_id = tokenClient.createMemberBlocking(alias,recoveryAgent).memberId();
//         System.out.println("member===="+member_id);
//         System.out.println("member===="+recoveryAgent);
        callbackContext.success(member_id);

        }catch(Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }

    }

    public TokenClient getTokenClient(Context context1){
        try {
            userAuthenticationStore = new UserAuthenticationStore(1000);
            System.out.println("is authenticated======"+userAuthenticationStore.isAuthenticated());
            userAuthenticationStore.authenticateUser();
            System.out.println("is authenticated======"+userAuthenticationStore.isAuthenticated());

            cryptoEngineFactory = new AKSCryptoEngineFactory(context1, userAuthenticationStore);

            if (tokenClient == null) {
                tokenClient = TokenClient.builder()
                        .devKey(developerKey)
                        .withCryptoEngine(cryptoEngineFactory)
                        .connectTo(cluster)
                        .timeout(20000)
                        .build();
            }
            System.out.println("token client"+tokenClient);
            return tokenClient;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public AliasProtos.Alias getAlias(String aliasType,String aliasValue){
        try {
            if(aliasType.equalsIgnoreCase("CUSTOM")) {
                type_user = AliasProtos.Alias.Type.CUSTOM;
            } else if(aliasType.equalsIgnoreCase("PHONE")) {
                type_user = AliasProtos.Alias.Type.PHONE;
            } else if(aliasType.equalsIgnoreCase("EMAIL")) {
                type_user = AliasProtos.Alias.Type.EMAIL;
            }
            alias = AliasProtos.Alias.newBuilder()
                    .setValue(aliasValue)
                    .setType(type_user)
                    .setRealm(realm)
                    .build();
            System.out.println("alias===="+alias);
            return alias;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Exception in alias"+e.getMessage());
            return null;
        }
    }

   private void getMember(JSONArray args,CallbackContext callbackContext){
        try{
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
            tokenClient = getTokenClient(context);
            Member member = tokenClient.getMemberBlocking(memberId);
            callbackContext.success("true");
        }catch (Exception e){
            //e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

       private void resolveAlias(JSONArray args,CallbackContext callbackContext){
        try{
            String aliasValue = new JSONObject(args.getString(0)).getString("aliasValue");
            String aliasType = new JSONObject(args.getString(0)).getString("aliasType");
            tokenClient = getTokenClient(context);
           alias = getAlias(aliasType,aliasValue);
//             alias = getAlias(aliasValue);
            String memberId = tokenClient.resolveAliasBlocking(alias).getId();
            callbackContext.success(memberId);
        }catch (Exception e){
            callbackContext.error(e.toString());
        }
    }
    
    public AliasProtos.Alias getBankAlias(){
        try {
            bankAlias = AliasProtos.Alias.newBuilder()
                    .setValue(realm)
                    .setType(type_bank)
                    .build();
            return bankAlias;
        }catch (Exception e){
            System.out.println("Exception in bank alias"+e.getMessage());
            return null;
        }
    }

    private String getBankMember(){
        try{
            recoveryAgent = tokenClient.resolveAliasBlocking(bankAlias).getId();
            System.out.println("Bank===="+recoveryAgent);
            return recoveryAgent;
        }catch (Exception e){
            return e.getMessage();
        }
    }

    private void linkAccounts(JSONArray args,CallbackContext callbackContext){
        JSONObject jsonObject;

        try {
            if(tokenClient ==null) {
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
            String accessToken = new JSONObject(args.getString(0)).getString("accessToken");
            Member member = tokenClient.getMemberBlocking(memberId);
            List<Account> accounts = member.linkAccountsBlocking(realm,accessToken);
            System.out.println("accounts====="+accounts);
            callbackContext.success("true");
        }catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
        }

        private void subscribe(JSONArray args,CallbackContext callbackContext){
            JSONObject jsonObject;
        try {
            if(tokenClient == null) {
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");

            String subscriber = tokenClient.getMemberBlocking(memberId).subscribeToNotificationsBlocking(realm).getId();
            callbackContext.success(subscriber);
        }catch (Exception e){
            callbackContext.error(e.toString());
        }

        }

        private void getAccounts(JSONArray args,CallbackContext callbackContext){
            JSONObject jsonObject;
            try {
            if(tokenClient == null) {
                System.out.println("=======================");
                System.out.println("getAccounts:Token client instance is null");
                System.out.println("=======================");
                tokenClient = getTokenClient(context);
            }
                String memberId = new JSONObject(args.getString(0)).getString("memberId");

                JSONArray jsonArray = new JSONArray();
            List<Account> accounts = tokenClient.getMemberBlocking(memberId).getAccountsBlocking();
            System.out.println("accounts===="+accounts);
            for (int i=0;i<accounts.size();i++){
                JSONObject accountObject = new JSONObject();
                accountObject.put("tokenAccountId",accounts.get(i).id());
                accountObject.put("bankAccountNumber",accounts.get(i).toProto().getAccountDetails().getIdentifier());
                accountObject.put("name",accounts.get(i).name());
                accountObject.put("supportsSendPayment",accounts.get(i).accountFeatures().getSupportsSendPayment());
                accountObject.put("isLocked",accounts.get(i).isLocked());
                jsonArray.put(accountObject);
            }
            System.out.println("array===="+jsonArray);
            callbackContext.success(jsonArray.toString());
        }
        catch (Exception e){
                e.printStackTrace();
            callbackContext.error(e.toString());
        }
        }

        private void unlinkAccounts(JSONArray args,CallbackContext callbackContext){
            JSONObject jsonObject;
            try {
                if(tokenClient == null) {
                    System.out.println("=======================");
                    System.out.println("Unlink Accounts:Token client instance is null");
                    System.out.println("=======================");
                    tokenClient = getTokenClient(context);
                }
                String memberId = new JSONObject(args.getString(0)).getString("memberId");
                JSONArray jsonArray =new JSONObject(args.getString(0)).getJSONArray("accounts");
                List<String> accountList = new ArrayList<>();
                for (int i=0;i<jsonArray.length();i++){
                    accountList.add(jsonArray.getString(i));
                }
                System.out.println("accountsd====="+jsonArray);
                System.out.println("accountsd====="+accountList);
                tokenClient.getMemberBlocking(memberId).unlinkAccountsBlocking(accountList);
                callbackContext.success("true");
            }catch (Exception e){
                e.printStackTrace();
                callbackContext.error(e.toString());
            }
        }

    private void deleteMember(JSONArray args,CallbackContext callbackContext){
        JSONObject jsonObject;
        try {
            if(tokenClient == null) {
                System.out.println("=======================");
                System.out.println("Unlink Accounts:Token client instance is null");
                System.out.println("=======================");
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");

            tokenClient.getMemberBlocking(memberId).deleteMemberBlocking();
            callbackContext.success("true");
        }catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void getTransfers(JSONArray args,CallbackContext callbackContext){
        JSONObject jsonObject;
        try {
            if(tokenClient == null) {
                System.out.println("=======================");
                System.out.println("getAccounts:Token client instance is null");
                System.out.println("=======================");
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");

            JSONArray jsonArray = new JSONArray();
            List<TransferProtos.Transfer> tranList = tokenClient.getMemberBlocking(memberId).getTransfersBlocking(null,100,null).getList();
            System.out.println("accounts===="+tranList);
            for (int i=0;i<tranList.size();i++){
                JSONObject accountObject = new JSONObject();
                accountObject.put("created_at_ms",tranList.get(i).getCreatedAtMs());
                accountObject.put("status",tranList.get(i).getStatus());
                accountObject.put("amountCurrency",tranList.get(i).getPayload().getAmount().getCurrency());
                accountObject.put("description",tranList.get(i).getPayload().getDescription());
                accountObject.put("amountVal",tranList.get(i).getPayload().getAmount().getValue());
                accountObject.put("tppMemberId",tranList.get(i).getPayloadSignatures(0).getMemberId());

                jsonArray.put(accountObject);
            }
            System.out.println("array===="+jsonArray);
            callbackContext.success(jsonArray.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void getConsents(JSONArray args,CallbackContext callbackContext){
        JSONObject jsonObject;
        try {
            if(tokenClient == null) {
                System.out.println("=======================");
                System.out.println("getAccounts:Token client instance is null");
                System.out.println("=======================");
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");

            JSONArray jsonArray = new JSONArray();
            List<TokenProtos.Token> consentList = tokenClient.getMemberBlocking(memberId).getAccessTokensBlocking(null,20).getList();
            System.out.println("accounts===="+consentList);
            for (int i=0;i<consentList.size();i++){
                JSONObject accountObject = new JSONObject();

                List list = consentList.get(i).getPayload().getAccess().getResourcesList();
                System.out.println("list size----------------" + list.size());
                JSONArray jsonArray1 = new JSONArray();
                for (int j = 0; j < list.size(); j++) {
                    String accountId = consentList.get(i).getPayload().getAccess().getResources(j).getAccount().getAccountId();

                    if (!accountId.isEmpty()) {
                        jsonArray1.put(consentList.get(i).getPayload().getAccess().getResources(j).getAccount().getAccountId());
                    } else {
                        System.out.println("Entered null for account id");
                    }
                }
                System.out.println("JSon===="+jsonArray1);
                accountObject.put("consentExpiry",consentList.get(i).getPayload().getExpiresAtMs());
                accountObject.put("accountList",jsonArray1);
                accountObject.put("tppMemberId",consentList.get(i).getPayload().getTo().getId());
                jsonArray.put(accountObject);
            }
            System.out.println("array===="+jsonArray);
            callbackContext.success(jsonArray.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }
    private void getProfile(JSONArray args,CallbackContext callbackContext){
        JSONObject jsonObject;
        try {
            if(tokenClient == null) {
                System.out.println("=======================");
                System.out.println("getAccounts:Token client instance is null");
                System.out.println("=======================");
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
            String tppMemberId = new JSONObject(args.getString(0)).getString("tppMemberId");

            MemberProtos.Profile profile = tokenClient.getMemberBlocking(memberId).getProfileBlocking(tppMemberId);
            System.out.println("accounts===="+profile);

            System.out.println("array===="+profile.getDisplayNameFirst());
            callbackContext.success(profile.getDisplayNameFirst());
        }
        catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void getProfilePicture(JSONArray args,CallbackContext callbackContext){
        JSONObject jsonObject;
        try {
            if(tokenClient == null) {
                System.out.println("=======================");
                System.out.println("getAccounts:Token client instance is null");
                System.out.println("=======================");
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
            String tppMemberId = new JSONObject(args.getString(0)).getString("tppMemberId");

            BlobProtos.Blob blob = tokenClient.getMemberBlocking(memberId).getProfilePictureBlocking(tppMemberId,MemberProtos.ProfilePictureSize.ORIGINAL);
            System.out.println("accounts===="+blob);
            System.out.println("accounts===="+blob.getPayload().getData().toByteArray());

            callbackContext.success(blob.getPayload().getData().toByteArray());
        }
        catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void getAccount(JSONArray args,CallbackContext callbackContext){
        JSONObject jsonObject;
        try {
            if(tokenClient == null) {
                System.out.println("=======================");
                System.out.println("getAccounts:Token client instance is null");
                System.out.println("=======================");
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
            String tokenAccountId = new JSONObject(args.getString(0)).getString("tokenAccountId");

            JSONArray jsonArray = new JSONArray();
             Account accountDetails = tokenClient.getMemberBlocking(memberId).getAccountBlocking(tokenAccountId);
            System.out.println("accounts===="+accountDetails);
                JSONObject accountObject = new JSONObject();
                accountObject.put("tokenAccountId",accountDetails.id());
                accountObject.put("bankAccountNumber",accountDetails.toProto().getAccountDetails().getIdentifier());
                accountObject.put("name",accountDetails.name());
                accountObject.put("supportsSendPayment",accountDetails.accountFeatures().getSupportsSendPayment());
                accountObject.put("isLocked",accountDetails.toProto().getIsLocked());
            System.out.println("accountObject===="+accountObject);
            callbackContext.success(jsonArray.toString());
        }
        catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void approveAccessToken(JSONArray args,CallbackContext callbackContext) {
        JSONObject jsonObject;
        try {
            if(tokenClient == null) {
                System.out.println("=======================");
                System.out.println("getAccounts:Token client instance is null");
                System.out.println("=======================");
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
            String tppMemberId = new JSONObject(args.getString(0)).getString("tppMemberId");
            String payload = new JSONObject(args.getString(0)).getString("payload");
//            String payload = "{\"payload\":\"{\\r\\n  \\\"tokenRequest\\\": {\\r\\n    \\\"requestPayload\\\": {\\r\\n      \\\"accessBody\\\": {\\r\\n        \\\"type\\\": [\\r\\n          \\\"ACCOUNTS\\\",\\r\\n          \\\"BALANCES\\\",\\r\\n          \\\"TRANSACTIONS\\\"\\r\\n        ]\\r\\n      },\\r\\n      \\\"callbackState\\\": \\\"%7B%22innerState%22%3A%7B%22a%22%3A1%7D%2C%22csrfTokenHash%22%3A%227MwSqP3EW5qkF8q378yinfNwYa8Gd8Y8Asv1qK1P1R4x%22%7D\\\",\\r\\n      \\\"refId\\\": \\\"6ulj64wsybs2kug929\\\",\\r\\n      \\\"to\\\": {\\r\\n        \\\"alias\\\": {\\r\\n          \\\"type\\\": \\\"DOMAIN\\\",\\r\\n          \\\"value\\\": \\\"finvertex.com.noverify\\\"\\r\\n        },\\r\\n        \\\"id\\\": \\\"m:9Eoc6Yda9881vKSMLTUkRViWzju:5zKtXEAq\\\"\\r\\n      }\\r\\n    },\\r\\n    \\\"id\\\": \\\"rq:2RThR74ENetnm1Lq1Gzf9vQWyzTg:5zKtXEAq\\\",\\r\\n    \\\"requestOptions\\\": {\\r\\n      \\\"bankId\\\": \\\"at-khal\\\",\\r\\n      \\\"from\\\": {\\r\\n        \\\"alias\\\": {\\r\\n          \\\"realm\\\": \\\"at-khal\\\",\\r\\n          \\\"type\\\": \\\"PHONE\\\",\\r\\n          \\\"value\\\": \\\"+97333263555\\\"\\r\\n        },\\r\\n        \\\"id\\\": \\\"m:2RQo4XSM8MuPo4ZxtyRE1xWa4Hwy:5zKtXEAq\\\"\\r\\n      }\\r\\n    }\\r\\n  },\\r\\n  \\\"contact\\\": {},\\r\\n  \\\"addKey\\\": {\\r\\n    \\\"keys\\\": [\\r\\n      {\\r\\n        \\\"level\\\": \\\"LOW\\\",\\r\\n        \\\"expiresAtMs\\\": \\\"1561994081362\\\",\\r\\n        \\\"id\\\": \\\"qsEnXurxkzUq_nlA\\\",\\r\\n        \\\"publicKey\\\": \\\"MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEMqjOgpKbFCJi-fMM49EsCaSNi6FzQlKmJ5E-iXO6m-wgB3zsS0cikU-p38wS2UlkmRzGR2TgT5LgMo04oLepQw\\\",\\r\\n        \\\"algorithm\\\": \\\"ECDSA_SHA256\\\"\\r\\n      }\\r\\n    ],\\r\\n    \\\"deviceMetadata\\\": {\\r\\n      \\\"applicationVersion\\\": \\\"74.0.3729.169\\\",\\r\\n      \\\"application\\\": \\\"Chrome\\\",\\r\\n      \\\"device\\\": \\\"Mac OS\\\"\\r\\n    }\\r\\n  }\\r\\n}\",\"type\":\"CREATE_AND_ENDORSE_TOKEN_ACCESS\"}";
            JSONArray jsonArray =new JSONObject(args.getString(0)).getJSONArray("accounts");
            List<String> accountList = new ArrayList<>();
            for (int i=0;i<jsonArray.length();i++){
                accountList.add(jsonArray.getString(i));
            }
//            accountList.add("a:Gnv5qeBpdwGpLJmWQoo8RYhMz9UnaTqtG4DymvMsL7L5:8QRouC2tRGXx");
            Member member =tokenClient.getMemberBlocking(memberId);

//            JSONObject jsonObject1 = new JSONObject(payload);
//            JSONObject jsonObject2 = new JSONObject(jsonObject1.getString("payload"));

            AccessTokenBuilder builder1;
            NotificationProtos.CreateAndEndorseToken content;
            NotificationProtos.CreateAndEndorseToken.Builder builder;

            builder = NotificationProtos.CreateAndEndorseToken.newBuilder();
            JsonFormat.parser().merge(payload,builder);
            content = builder.build();

            builder1 = AccessTokenBuilder.fromTokenRequest(content.getTokenRequest());
            for(int i=0;i<accountList.size();i++) {
                builder1.forAccountBalances(accountList.get(i));
                builder1.forAccount(accountList.get(i));
                builder1.forAccountTransactions(accountList.get(i));
            }

//            TokenProtos.Token token = member.getActiveAccessTokenBlocking(tppMemberId);
//            TokenProtos.TokenOperationResult replaceToken = member.replaceAccessTokenBlocking(token,builder1);

//            member.endorseTokenBlocking(replaceToken.getToken(), SecurityProtos.Key.Level.STANDARD);
cordova.getActivity().runOnUiThread(new Runnable() {
    @Override
    public void run() {
        tokenClient.getMember(memberId)
                .subscribe(new Consumer<Member>() {
                    @Override
                    public void accept(final Member member) throws Exception {
                        member.getActiveAccessToken(tppMemberId)
                                .subscribe(new Consumer<TokenProtos.Token>() {
                                    @Override
                                    public void accept(TokenProtos.Token token) throws Exception {
                                        System.out.println("token pay---->" + token);
                                        System.out.println("token pay---->" + token.getPayload());
//
                                        member.replaceAccessToken(token, builder1)
                                                .subscribe(new Consumer<TokenProtos.TokenOperationResult>() {
                                                    @Override
                                                    public void accept(TokenProtos.TokenOperationResult replaceToken) throws Exception {
                                                        System.out.println("Replace token" + replaceToken.getToken());
                                                        member.endorseToken(replaceToken.getToken(), SecurityProtos.Key.Level.STANDARD)
                                                                .flatMap(new Function<TokenProtos.TokenOperationResult, ObservableSource<?>>() {
                                                                    @Override
                                                                    public ObservableSource<?> apply(TokenProtos.TokenOperationResult tokenOperationalResult) throws Exception {
                                                                        return member.signTokenRequestState(content.getTokenRequest().getId(),
                                                                                tokenOperationalResult.getToken().getId(),
                                                                                content.getTokenRequest().getRequestPayload().getCallbackState());
                                                                    }
                                                                }).subscribe(new Consumer<Object>() {
                                                                                 @Override
                                                                                 public void accept(Object ignore) throws Exception {
                                                                                     callbackContext.success("true");

                                                                                 }
                                                                             }
                                                        );
                                                    }
                                                }, new Consumer<Throwable>() {
                                                    @Override
                                                    public void accept(Throwable onError) throws Exception {
                                                        System.out.println("Error while replace access token----" + onError);
                                                        System.out.println("Error while replace access token----" + onError.getMessage());
                                                        callbackContext.error(onError.toString());
                                                    }
                                                });
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable onError) throws Exception {
                                        member.createAccessToken(builder1)
                                                .subscribe(new Consumer<TokenProtos.Token>() {
                                                    @Override
                                                    public void accept(TokenProtos.Token token) throws Exception {
                                                        System.out.println("");
                                                        member.endorseToken(token, SecurityProtos.Key.Level.STANDARD)
                                                                .flatMap(new Function<TokenProtos.TokenOperationResult, ObservableSource<?>>() {
                                                                    @Override
                                                                    public ObservableSource<?> apply(TokenProtos.TokenOperationResult tokenOperationalResult) throws Exception {
                                                                        return member.signTokenRequestState(
                                                                                content.getTokenRequest().getId(),
                                                                                tokenOperationalResult.getToken().getId(),
                                                                                content.getTokenRequest().getRequestPayload().getCallbackState());
                                                                    }
                                                                })
                                                                .subscribe(new Consumer<Object>() {
                                                                    @Override
                                                                    public void accept(Object ignore) throws Exception {
                                                                        System.out.println("Ignore----" + ignore);
                                                                        callbackContext.success("true");
                                                                    }
                                                                }, new Consumer<Throwable>() {
                                                                    @Override
                                                                    public void accept(Throwable onError) throws Exception {
                                                                        System.out.println("Error" + onError.getMessage());
                                                                        callbackContext.error(onError.toString());
                                                                    }
                                                                });
                                                    }
                                                });

                                    }
                                });

                    }
                });
    }
    });
            callbackContext.success("true");
        } catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void cancelAccessToken(JSONArray args,CallbackContext callbackContext) {

            JSONObject jsonObject;
            try {
                if(tokenClient == null) {
                    System.out.println("=======================");
                    System.out.println("getAccounts:Token client instance is null");
                    System.out.println("=======================");
                    tokenClient = getTokenClient(context);
                }
                String memberId = new JSONObject(args.getString(0)).getString("memberId");
                String tppMemberId = new JSONObject(args.getString(0)).getString("tppMemberId");

                Member member =tokenClient.getMemberBlocking(memberId);
                TokenProtos.Token token = member.getActiveAccessTokenBlocking(tppMemberId);
                member.cancelTokenBlocking(token);

                callbackContext.success("true");
        }catch (Exception e){
                e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void approveTransferToken(JSONArray args,CallbackContext callbackContext) {

        JSONObject jsonObject;
        try {
            if (tokenClient == null) {
                System.out.println("=======================");
                System.out.println("getAccounts:Token client instance is null");
                System.out.println("=======================");
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
//            String tppMemberId = new JSONObject(args.getString(0)).getString("tppMemberId");
            String payload = new JSONObject(args.getString(0)).getString("payload");
            String account = new JSONObject(args.getString(0)).getString("account");
            

//            JSONObject jsonObject2 = new JSONObject(payload);
//            System.out.println("json object in per"+jsonObject2);
//            JSONObject jsonObject3 = new JSONObject(jsonObject2.getString("payload"));
//            System.out.println("json object in after payload----"+jsonObject3);


            builder = NotificationProtos.CreateAndEndorseToken.newBuilder();
            JsonFormat.parser().merge(payload, builder);
            content = builder.build();

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                                tokenClient.getMember(memberId)
                                        .subscribe(new Consumer<Member>() {
                                            @Override
                                            public void accept(final Member member) throws Exception {
                                                member.createTransferToken(content.getTokenRequest())
                                                        .setAccountId(account)
                                                        .execute()
                                                        .subscribe(new Consumer<TokenProtos.Token>() {
                                                            @Override
                                                            public void accept(TokenProtos.Token token) throws Exception {
                                                                member.endorseToken(token, SecurityProtos.Key.Level.STANDARD)
                                                                        .flatMap(new Function<TokenProtos.TokenOperationResult, ObservableSource<?>>() {
                                                                            @Override
                                                                            public ObservableSource<?> apply(TokenProtos.TokenOperationResult tokenOperationalResult) throws Exception {
                                                                                return member.signTokenRequestState(
                                                                                        content.getTokenRequest().getId(),
                                                                                        tokenOperationalResult.getToken().getId(),
                                                                                        content.getTokenRequest().getRequestPayload().getCallbackState());
                                                                            }
                                                                        })
                                                                        .subscribe(new Consumer<Object>() {
                                                                            @Override
                                                                            public void accept(Object ignore) throws Exception {
                                                                                System.out.println("Ignore----" + ignore);
                                                                                callbackContext.success("true");
                                                                            }

                                                                        }, new Consumer<Throwable>() {
                                                                            @Override
                                                                            public void accept(Throwable onError) throws Exception {
                                                                                System.out.println("Error" + onError.getMessage());
                                                                                callbackContext.error(onError.toString());
                                                                            }
                                                                        });
                                                            }
                                                        });
                                            }
                                        });
                }
            });
        } catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

   private void memberRecovery(JSONArray args,CallbackContext callbackContext){

        try {
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
            tokenClient = getTokenClient(context);

            cryptoEngine = new AKSCryptoEngineFactory(context,userAuthenticationStore,true).create(memberId);
//             cryptoEngine.deleteKeys();
            privilegedKey = cryptoEngine.generateKey(SecurityProtos.Key.Level.PRIVILEGED);
            authorization = tokenClient.createRecoveryAuthorizationBlocking(memberId, privilegedKey);
            MemberProtos.MemberRecoveryOperation.Authorization.Builder signatureBuilder = authorization.toBuilder();
            //System.out.println("privileged key==="+privilegedKey);
            //System.out.println("cryptoEngine key==="+cryptoEngine);
            callbackContext.success(JsonFormat.printer().print(signatureBuilder));
        }catch (Exception e){
            //e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

  private void getRecoveredMember(JSONArray args,CallbackContext callbackContext){
        try {
            String mro = new JSONObject(args.getString(0)).getString("mro");
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
            tokenClient = getTokenClient(context);
            //System.out.println("privileged key=== in recover"+privilegedKey);
            //System.out.println("cryptoEngine key=== in recover"+cryptoEngine);

            MemberProtos.MemberRecoveryOperation.Builder mroBuilder = MemberProtos.MemberRecoveryOperation.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(mro, mroBuilder);
            Message message = mroBuilder.build();
            MemberProtos.MemberRecoveryOperation mro1 = (MemberProtos.MemberRecoveryOperation) message;

            Member recoveredMember = tokenClient.completeRecoveryBlocking(
                    memberId,
                    Arrays.asList(mro1),
                    privilegedKey,
                    cryptoEngine);
            //System.out.println("recovered member==="+recoveredMember.memberId());
            callbackContext.success(recoveredMember.memberId());

        }catch (Exception e){
            //e.printStackTrace();

            callbackContext.error(e.toString());
        }
    }

 private void provisionRequest(JSONArray args,CallbackContext callbackContext){
        try {
//             String mobileNumber = new JSONObject(args.getString(0)).getString("mobileNumber");
            String aliasValue = new JSONObject(args.getString(0)).getString("aliasValue");
            String aliasType = new JSONObject(args.getString(0)).getString("aliasType");

            final NotificationProtos.DeviceMetadata deviceMetadata = NotificationProtos.DeviceMetadata.newBuilder()
                    .setApplication("token")
                    .setDevice("android")
                    .build();
            tokenClient = getTokenClient(context);
            alias = getAlias(aliasType,aliasValue);
            String memberId = tokenClient.resolveAliasBlocking(alias).getId();
            cryptoEngine = new AKSCryptoEngineFactory(context,userAuthenticationStore,true).create(memberId);
//             cryptoEngine.deleteKeys();
            tokenClient.provisionDevice(alias)
                    .flatMap(new Function<DeviceInfo, ObservableSource<?>>() {
                        @Override
                        public ObservableSource<?> apply(DeviceInfo deviceInfo) throws Exception {
                            return tokenClient.notifyAddKey(alias, deviceInfo.getKeys(), deviceMetadata);
                        }
                    })
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object notifyStatus) throws Exception {
                            //System.out.println("notify status --->"+notifyStatus);
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable onError) throws Exception {
                            //System.out.println("onError--->"+onError.getMessage());
                            callbackContext.error(onError.toString());
                        }
                    }, new Action() {
                        @Override
                        public void run() throws Exception {
                            callbackContext.success("true");
                        }
                    });
//            callbackContext.success("true");
        }catch (Exception e){
            //e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

    private void provisionResponse(JSONArray args,CallbackContext callbackContext) {
        try {
//             String mobileNumber = new JSONObject(args.getString(0)).getString("mobileNumber");
            String aliasValue = new JSONObject(args.getString(0)).getString("aliasValue");
            String aliasType = new JSONObject(args.getString(0)).getString("aliasType");
            String payload = new JSONObject(args.getString(0)).getString("payload");
            String memberId = new JSONObject(args.getString(0)).getString("memberId");

            tokenClient = getTokenClient(context);
            alias = getAlias(aliasType,aliasValue);

            NotificationProtos.AddKey.Builder builder = NotificationProtos.AddKey.newBuilder();
            JsonFormat.parser().merge(payload, builder);
            addkey = builder.build();

            tokenClient.getMember(memberId)
                    .subscribe(new Consumer<Member>() {
                        @Override
                        public void accept(Member member) throws Exception {
                            member.approveKeys(addkey.getKeysList())
                                    .subscribe(new Action() {
                                        @Override
                                        public void run() throws Exception {
                                            callbackContext.success("true");
                                        }
                                    });
                        }
                    },onError->{
                        callbackContext.error(onError.toString());
                    });

//            callbackContext.success("true");
        }catch (Exception e){
            //e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }



private void onAccountRevoke(JSONArray args,CallbackContext callbackContext) {
        JSONObject jsonObject;
        try {
            if (tokenClient == null) {
                tokenClient = getTokenClient(context);
            }
            String memberId = new JSONObject(args.getString(0)).getString("memberId");
            String tppMemberId = new JSONObject(args.getString(0)).getString("tppMemberId");
            JSONArray jsonArray = new JSONObject(args.getString(0)).getJSONArray("accounts");

            List<String> accountList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                accountList.add(jsonArray.getString(i));
            }

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tokenClient.getMember(memberId)
                            .subscribe(new Consumer<Member>() {
                                @Override
                                public void accept(final Member member1) throws Exception {
                                    member1.getActiveAccessToken(tppMemberId)
                                            .subscribe(new Consumer<TokenProtos.Token>() {
                                                @Override
                                                public void accept(TokenProtos.Token token) throws Exception {
                                                    AccessTokenBuilder accessTokenBuilder = AccessTokenBuilder.fromPayload(token.getPayload());
                                                    for (int i = 0; i < accountList.size(); i++) {
                                                        accessTokenBuilder.forAccount(accountList.get(i));
                                                        accessTokenBuilder.forAccountTransactions(accountList.get(i));
                                                        accessTokenBuilder.forAccountBalances(accountList.get(i));
                                                    }
                                                    member1.replaceAccessToken(token, accessTokenBuilder)
                                                            .subscribe(new Consumer<TokenProtos.TokenOperationResult>() {
                                                                @Override
                                                                public void accept(TokenProtos.TokenOperationResult replaceToken) throws Exception {
                                                                    member1.endorseToken(replaceToken.getToken(), SecurityProtos.Key.Level.STANDARD)
                                                                            .subscribe(new Consumer<TokenProtos.TokenOperationResult>() {
                                                                                @Override
                                                                                public void accept(TokenProtos.TokenOperationResult replacedToken) throws Exception {
                                                                                    callbackContext.success("true");
                                                                                }
                                                                            }, new Consumer<Throwable>() {
                                                                                @Override
                                                                                public void accept(Throwable onError) throws Exception {
                                                                                    callbackContext.error(onError.getMessage());
                                                                                }
                                                                            });
                                                                }
                                                            }, new Consumer<Throwable>() {
                                                                @Override
                                                                public void accept(Throwable onError) throws Exception {
                                                                    callbackContext.error(onError.getMessage());

                                                                }
                                                            }, new Action() {
                                                                @Override
                                                                public void run() throws Exception {
                                                                }
                                                            });
                                                }
                                            });
                                }
                            });
                }
            });
        } catch (Exception e){
            //e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }

}
