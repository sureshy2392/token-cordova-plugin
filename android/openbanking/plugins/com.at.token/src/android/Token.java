package com.at.tokencordova;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.token.security.AKSCryptoEngineFactory;
import io.token.security.CryptoEngineFactory;
import io.token.security.UserAuthenticationStore;
import io.token.user.TokenClient;
// import custom.TokenClient;

import io.token.proto.common.alias.AliasProtos;
// import io.reactivex.android.schedulers.AndroidSchedulers;
// import io.reactivex.functions.Action;
// import io.reactivex.functions.Consumer;
import android.content.Context;
import io.token.proto.common.token.TokenProtos;
import io.token.proto.common.alias.AliasProtos;

import android.widget.Toast;


import java.io.IOException;

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


  public UserAuthenticationStore userAuthenticationStore;
    public CryptoEngineFactory cryptoEngineFactory;
    public TokenClient tokenClient;
    public AliasProtos.Alias alias,bankAlias;
    public String member_id;
    public static final String developerKey = "4qY7lqQw8NOl9gng0ZHgT4xdiDqxqoGVutuZwrUYQsI";
    public static final String realm = "at-bisb";
    public static final String recoveryAgent = "m:4A6NpTk5XS3GuUEdjMZSTEWpjKD6:5zKtXEAq";
    public static final AliasProtos.Alias.Type type_user = AliasProtos.Alias.Type.PHONE;
    public static final AliasProtos.Alias.Type type_bank = AliasProtos.Alias.Type.BANK;
    public static final io.token.TokenClient.TokenCluster cluster = io.token.TokenClient.TokenCluster.SANDBOX;
    public String mobileNumber ="+97333263451";
    Context context;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        context = this.cordova.getActivity().getApplicationContext();

        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        else if (action.equals("createMember")) {
            this.createMember(callbackContext);
            return true;

        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

      private void createMember(CallbackContext callbackContext) {

         try {
                tokenClient = getTokenClient(context);
                alias = getAlias();
                member_id = tokenClient.createMemberBlocking(alias,recoveryAgent).memberId();
                callbackContext.success(member_id);
        }catch(Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }

    }
    public String getMember(){

        try {
            member_id = tokenClient.createMemberBlocking(alias,recoveryAgent).memberId();
            return member_id;
        }
        catch (Exception e){
            return e.toString();
        }
    }

    public TokenClient getTokenClient(Context context1){
        try {

            userAuthenticationStore = new UserAuthenticationStore(10000);
            userAuthenticationStore.authenticateUser();
            cryptoEngineFactory = new AKSCryptoEngineFactory(context, userAuthenticationStore);

            if (tokenClient == null) {
                tokenClient = TokenClient.builder()
                        .devKey(developerKey)
                        .withCryptoEngine(cryptoEngineFactory)
                        .connectTo(cluster)
                        .build();
            }
            System.out.println("token client"+tokenClient);
            return tokenClient;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public AliasProtos.Alias getAlias(){
        try {
            alias = AliasProtos.Alias.newBuilder()
                    .setValue(mobileNumber)
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
}
