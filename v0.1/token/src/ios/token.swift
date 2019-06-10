import TokenSdk

class token: CDVPlugin {
    let developerKey: NSString = "4qY7lqQw8NOl9gng0ZHgT4xdiDqxqoGVutuZwrUYQsI"
    let env: TokenCluster = TokenCluster.sandbox()
    let realm = "at-bisb"
    
    // create and return a token client
    
    func getTokenClient() -> TokenClient {
        let builder: TokenClientBuilder?
        builder = TokenClientBuilder()
        builder?.tokenCluster = TokenCluster.sandbox()
        builder?.port = 443
        builder?.useSsl = true
        builder?.developerKey = "4qY7lqQw8NOl9gng0ZHgT4xdiDqxqoGVutuZwrUYQsI"
        return (builder?.build())!
    }
    
    // create and return an alias object
    
    func makeAliasObject(value: String) -> Alias {
        let alias = Alias()
        alias.type = Alias_Type.phone
        alias.realm = realm
        alias.value = value
        return alias
    }
    
    // create a member and return memberId
    
    @objc(createMember:)
    func createMember(mobileNumber: CDVInvokedUrlCommand) {
        var memberId = ""
        
        let mobileNumberStr = mobileNumber.arguments[0] as? String ?? ""
        
        print("creating member with alias", mobileNumberStr)
        
        let dispatchGrp = DispatchGroup()
        dispatchGrp.enter()
        
        getTokenClient().createMember(makeAliasObject(value: "+97333209987"), recoveryAgent: realm, onSuccess: { TKMember in
            print("createMember:success", TKMember.id)
            memberId = TKMember.id
            
            dispatchGrp.leave()
        }, onError: { Error in
            print("createMember:failed", Error)
            dispatchGrp.leave()
        })
        
        dispatchGrp.notify(queue: .main) {
            print("main notified with member: ", memberId)
            var pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: memberId
            )
            self.commandDelegate!.send(
                pluginResult,
                callbackId: mobileNumber.callbackId
            )
        }
    }
    
    @objc(subscribe:)
    func subscribe(memberId: CDVInvokedUrlCommand) {
        var subscriberId = ""
        let memberIdStr = memberId.arguments[0] as? String ?? ""
        
        print("subscribing for member ", memberIdStr)
        
        let dispatchGrp = DispatchGroup()
        dispatchGrp.enter()
        
        getTokenClient().getMember(memberIdStr, onSuccess: { Member in
            Member.subscribe(toNotifications: self.realm, handlerInstructions: nil, onSuccess: { Subscriber in
                subscriberId = Subscriber.id_p
                print("subscriber id is ", Subscriber.id_p)
                dispatchGrp.leave()
                
            }, onError: { Error in
                print("subcription error ", Error)
                dispatchGrp.leave()
                
            })
        }, onError: { Error in
            print("error during subcription ", Error)
            dispatchGrp.leave()
            
        })
        
        dispatchGrp.notify(queue: .main) {
            print("main notified with member: ", subscriberId)
            var pluginResult = CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: subscriberId
            )
            self.commandDelegate!.send(
                pluginResult,
                callbackId: memberId.callbackId
            )
        }
    }
}
