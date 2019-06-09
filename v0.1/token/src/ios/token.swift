import TokenSdk

class token : CDVPlugin {
  @objc(createMember:)
    
    
  func createMember(mobileNumber: CDVInvokedUrlCommand) {

        print("received mobileNumber",mobileNumber)
   
        let taskQ = DispatchQueue(label: "perform_task")
    
        taskQ.sync {

            print("create a token client object")

            var builder :TokenClientBuilder?
            builder = TokenClientBuilder()
            builder?.tokenCluster = TokenCluster.sandbox()
            builder?.port = 443
            builder?.useSsl = true
            builder?.developerKey = "4qY7lqQw8NOl9gng0ZHgT4xdiDqxqoGVutuZwrUYQsI"

            var tokenClient :TokenClient?
            tokenClient = builder?.build()
            print("token client",tokenClient)

            var alias  = Alias()
            alias.type = Alias_Type.phone
            alias.realm = "at-bisb"
            alias.value = "+97333263343"

            print("passed alias",alias)

            tokenClient?.createMember(alias, recoveryAgent: "at-bisb", onSuccess: { (TKMember) in
                print("created a member",TKMember)
            }, onError: { (Error) in
                print("failed to created a member",Error)
            })
            
        }

    var pluginResult = CDVPluginResult(
        status: CDVCommandStatus_ERROR
    )
    
    
    self.commandDelegate!.send(
        pluginResult,
        callbackId: mobileNumber.callbackId
    )
 
  }
    
    
    
}
