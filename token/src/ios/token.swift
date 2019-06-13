import TokenSdk

class token: CDVPlugin {
    // global settings
    static let developerKey: String = "4qY7lqQw8NOl9gng0ZHgT4xdiDqxqoGVutuZwrUYQsI"
    static let env: TokenCluster = TokenCluster.sandbox()
    static let realm = "at-bisb"
    static let recoveryAgent = "m:4A6NpTk5XS3GuUEdjMZSTEWpjKD6:5zKtXEAq"
    static let aliasType = Alias_Type.phone
    static let testRealm = "at-kfho"
    
    // create and return a token client object
    
    func getTokenClient() -> TokenClient {
        let builder: TokenClientBuilder?
        builder = TokenClientBuilder()
        builder?.tokenCluster = token.env
        builder?.port = 443
        builder?.useSsl = true
        builder?.developerKey = token.developerKey
        return (builder?.build())!
    }
    
    // create and return an alias object
    
    func makeAliasObject(value: String) -> Alias {
        let alias = Alias()
        alias.type = token.aliasType
        alias.realm = token.realm
        alias.value = value
        return alias
    }
    
    // create a member and return memberId
    
    @objc(createMember:)
    func createMember(argList: CDVInvokedUrlCommand) {
        // local vars
        var memberId = String()
        var pluginResult = CDVPluginResult()
        var error = String()
        let dispatchGrp = DispatchGroup()
        dispatchGrp.enter()
        
        // read from args
        var mobileNumberStr = argList.arguments[0] as? String ?? ""
        mobileNumberStr = mobileNumberStr.trimmingCharacters(in: CharacterSet.whitespaces)
        print("creating member with alias", mobileNumberStr)
        
        // create a member and return member id
        getTokenClient().createMember(makeAliasObject(value: mobileNumberStr), recoveryAgent: token.recoveryAgent, onSuccess: { TKMember in
            print("createMember:success", TKMember.id)
            memberId = TKMember.id
            dispatchGrp.leave()
        }, onError: { Error in
            print("createMember:failed", Error.localizedDescription)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId)
            if memberId == "" {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: memberId
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argList.callbackId
            )
        }
    }
    
    // subscribe for notification and return subscriber id
    
    @objc(subscribe:)
    func subscribe(argList: CDVInvokedUrlCommand) {
        // local vars
        var subscriberId = String()
        var pluginResult = CDVPluginResult()
        var error = String()
        let dispatchGrp = DispatchGroup()
        dispatchGrp.enter()
        
        // read from args
        let memberId = argList.arguments[0] as? String ?? ""
        print("subscribing for member ", memberId)
        
        // subscribe to token and return subscriber id
        getTokenClient().getMember(memberId, onSuccess: { Member in
            Member.subscribe(toNotifications: token.realm, handlerInstructions: nil, onSuccess: { Subscriber in
                subscriberId = Subscriber.id_p
                print("subscriber id is ", Subscriber.id_p)
                dispatchGrp.leave()
                
            }, onError: { Error in
                print("subcription error ", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("error during subcription ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
            
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId)
            if subscriberId == "" {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: subscriberId
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argList.callbackId
            )
        }
    }
    
    // pass access token and link accounts
    
    @objc(linkAccounts:)
    func linkAccounts(argList: CDVInvokedUrlCommand) {
        // local vars
        var status = Bool()
        var pluginResult = CDVPluginResult()
        var error = String()
        let dispatchGrp = DispatchGroup()
        dispatchGrp.enter()
        
        // read from args
        
        var argArray = (argList.arguments[0] as AnyObject) as! [Any]
        print("received arguments", argArray)
        
        var memberId: String = argArray[0] as! String
        var accessToken: String = (argArray[1] as! String)+"|"+memberId
        
        print("linking accounts to member ",memberId," using at: ", accessToken)
        
        getTokenClient().getMember(memberId, onSuccess: { Member in
            Member.linkAccounts(token.testRealm, accessToken: accessToken, onSuccess: { accounts in
                print("accounts linked: ", accounts)
                if accounts != nil {
                    status = true
                    print("linked: ", status)
                    dispatchGrp.leave()
                }
            }, onError: { Error in
                print("error during linking ", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during linking ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: status
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argList.callbackId
            )
        }
    }
    
    // pass member id to get the list of accounts
    
    @objc(getAccounts:)
    func getAccounts(argList: CDVInvokedUrlCommand) {
        // local vars
        var status = Bool()
        var pluginResult = CDVPluginResult()
        var error = String()
        var fetchedList = Array<Any>()
        let dispatchGrp = DispatchGroup()
        dispatchGrp.enter()
        
        // read from args
        let memberId = argList.arguments[0] as? String ?? ""
        print("fetching linked accounts for member: ", memberId)
        
        getTokenClient().getMember(memberId, onSuccess: { Member in
            Member.getAccounts({ accountList in
                print("fetched accounts: ", accountList)
                fetchedList = accountList
                status = true
                dispatchGrp.leave()
            }, onError: { Error in
                print("error during fetching linked accounts ", Error)
                error = Error.localizedDescription
                dispatchGrp.leave()
            })
        }, onError: { Error in
            print("member lookup error during fetching linked accounts ", Error)
            error = Error.localizedDescription
            dispatchGrp.leave()
        })
        
        // wait for async and notify main
        dispatchGrp.notify(queue: .main) {
            print("main notified by member: ", memberId,status)
            if status == true {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_OK,
                    messageAs: fetchedList
                )
            } else {
                pluginResult = CDVPluginResult(
                    status: CDVCommandStatus_ERROR,
                    messageAs: error
                )
            }
            self.commandDelegate!.send(
                pluginResult,
                callbackId: argList.callbackId
            )
        }
    }
}
