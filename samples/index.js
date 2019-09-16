/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
        
        //create member call from cordova
//        token.createMember(
//                           {'mobileNumber':'+97333263351'},
                            {'aliasType':'CUSTOM','aliasValue':'123456789'},
//                           function(msg) {
//                           console.log("success",msg);
//                           },
//                           function(msg) {
//                           console.log("failure",msg);
//
//                           }
//                           );
        
//        //subscription call from cordova
//        token.subscribe(
//                        {'memberId':'m:rR7QS4x3keacg9UhDd1XcVYe44z:5zKtXEAq'},
//                           function(msg) {
//                           console.log("success",msg);
//                           },
//                           function(msg) {
//                           console.log("failure",msg);
//
//                           }
//                           );
        
        
       // link accounts call - pass AT
//        token.linkAccounts(
//                           {'memberId':'m:2xX2GsxnRDE4SYYRWJw7yDHdzv3p:5zKtXEAq','accessToken':'JQ+Hwn88Qp+V4ZW3F+CzT3wDUzGDdmBVuWaxm+VM78NE6mYgQshog8TT3FNgUiWhRixeKeB+G/UYhiKlJSj2razKhYYVIqME3JkhfnOO5X4LkmW7RjK7w0SAPtJdgzy8Pz65MBZcEOhWDY8vjaODfY99qmpETWj7J/8vTk21Stta7IQr1mYt5WQ8shf14PJSmdCAIT690jbnYBl6PlLcfjKzsUhF3IQa0NJG2exfuzr6Q/KSsotCCp+zLsmhYJ8KvbkgOjgp9fXW2PhOcUhi/apUu0jzm8Pec77Fe5aLiEqxRLw8qiAuXcmIEC22vTS24i0xPSqMdYL0xf2dA+68Bg=='},
//                        function(msg) {
//                        console.log("success",msg);
//                        },
//                        function(msg) {
//                        console.log("failure",msg);
//
//                        }
//                        );

        
        //link accounts call - pass AT
        //        token.linkAccounts(
        //                           {'memberId':'m:kzPExkVTHWxscnQNhQCzD74Gdob:5zKtXEAq','accessToken':'UAi4bPyCjCTyyTAFQvHqpZ6icRdVwF9Q+4tOQAXWC6PPODsjlQ/FFQO7U19ozPwmHuyRuXbatRzj71Z0OfR3ABR9new7A13t81Ell1srdC6Gxlaj2OFO7fqDSiEXZN1/FVTfC8JqJKMF0qrFxBtN5kCiHRI7m1zHfUYHlKF8QHyAWQXJNukyZEJvqOxjM3YqZOvuXoAtgz9QZ8/LYo25FFyRU1e0MfX/4GhDzx9e0a4YrHC4dTsyS4BhLvovzQatweGi8fQwvZ1IJn0GlfS5MkjPtGTkKUvKKIkYipFRQLeHYXaR9oAD5i1t7NjqN5H42DuwNco1otK2qHhjZfuGXamQflCRvu1ZtNwvX6O5VS/MvEuBCTDxGYW/bq6qOmNVwiYXfbtzcpUOHxDQToy1yBmleiSzjd/6byCJhRaPcWBwX8y0Gr22nhsTQmVinLkgLlAcOODvO5CPR1ZQ0LlLRYK9p7d8h0P7az5e9+TTJJdhc9cEBngV2+v1fclNiYpoCrcIWetiUrASA62DkZcYyw9ei0SNppGUWM+l+Usi0Cvnoj6D2dJwMB8WbQFal41SoMxiFHFKWT2IqSXbPBxykSNMi3abK7SV5rpXlZPMlMFhzC36Kzh6TxKlXhQ/cCnDlQk4fNe9KBewyob+mCRRQgDsjy9szNxVMcwRHh9WMHmDPhf63Xs6nIjL0k+xkWBGygicFwrSyfx1hMSE3HbovxGydTxEU1veEpPyoDJxjqJFCRnxZeYD/oc3o7A7NolMa2ImSWLwgRheILAIlctn16Aiy+z/Hpe9zYAWVyKPdjbGri5nWQvLBkOnLjmo2vgjhTHixE9JMu3Gya37CTN1fCBErh0s5D7YXg47ybesxY8O4Z3GHn/H1dbpHuNaXvHsIMJslthvxRJyTJtlKMmVMP0ZE0IjNKZjel1xpsZgqLpLZDWPtKO2w95hgD7AUI96O0y/H1CKcMa0+HFjazjmMHoZ8EGw1vsz6MrFp9uK7SuzWEkNSBNVdAUWdXDTZ3q8Us/G8NOXDU1i+v1b6+J/pGCg0ZPOSBi6+JDGq1vpTEs1hlllfXWWeGtQKlKB91sPTJu+Lgvz5rK+d2q9nIA/t1FEJZSH0Qium0SZ8Ix1gw1illp+a+LjE85FoEbruAz2161xUNuWVVYwKAd2tncPrQgOJBtjHgO4PISFQoWH1sHMvlflkZBf5+J2GjhG1tknBhThdsaWUxB1+vHuMFGisUgl3Y6HVSax99F8pa1H4NOmqrSkmSOGyS34WuEQgk6jI+xoyMU9E03CfmyYyY9wWo/0BtROu1kOx0XSMhL1AQ/494ErrSveCCbw2JlruX3rYJ2BCvKG7aXxA42klN5I4w=='},
        //                        function(msg) {
        //                        console.log("success",msg);
        //                        },
        //                        function(msg) {
        //                        console.log("failure",msg);
        //
        //                        }
        //                        );
        
        
                //get linked accounts
//                token.getAccounts(
//                                  {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq'},
//                                   function(msg) {
//                                   console.log("success",msg);
//                                   },
//                                   function(msg) {
//                                   console.log("failure",msg);
//
//                                   }
//                                   );
 
        
        //get account
//                        token.getAccount(
//                                          {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq','tokenAccountId':'a:DDmcyTDdV9EjLac1WTAJeBU2uLsMnWetix42v6pjZSqp:8QRouC2qXzi5'},
//                                           function(msg) {
//                                           console.log("success",msg);
//                                           },
//                                           function(msg) {
//                                           console.log("failure",msg);
//
//                                           }
//                                           );
        
        
        //get aisp consents
//        token.getConsents(
//                          {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq'},
//                          function(msg) {
//                          console.log("success",msg);
//                          },
//                          function(msg) {
//                          console.log("failure",msg);
//
//                          }
//                          );

//        {'memberId':'m:2xX2GsxnRDE4SYYRWJw7yDHdzv3p:5zKtXEAq','accounts':['a:FYKyDCCKqpc6fhvNVCh4oBqhR5w4i3m6wn1FB6Vo3HuP:8QRouC2qXzi5','a:DDmcyTDdV9EjLac1WTAJeBU2uLsMnWetix42v6pjZSqp:8QRouC2qXzi5']'},
//
//
        
        //unlink accounts
//        token.unlinkAccounts(
//                          {'memberId':'m:2xX2GsxnRDE4SYYRWJw7yDHdzv3p:5zKtXEAq','accounts':['a:FYKyDCCKqpc6fhvNVCh4oBqhR5w4i3m6wn1FB6Vo3HuP:8QRouC2qXzi5','a:DDmcyTDdV9EjLac1WTAJeBU2uLsMnWetix42v6pjZSqp:8QRouC2qXzi5']},
//                          function(msg) {
//                          console.log("success",msg);
//                          },
//                          function(msg) {
//                          console.log("failure",msg);
//
//                          }
//                          );
        
        
        //get transfers
//        token.getTransfers(
//                          {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq'},
//                          function(msg) {
//                          console.log("success",msg);
//                          },
//                          function(msg) {
//                          console.log("failure",msg);
//
//                          }
//                          );
        
        
        //get profile
//                token.getProfile(
//                                 {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq','tppMemberId':'m:9Eoc6Yda9881vKSMLTUkRViWzju:5zKtXEAq'},
//                                  function(msg) {
//                                  console.log("success",msg);
//                                  },
//                                  function(msg) {
//                                  console.log("failure",msg);
//
//                                  }
//                                  );
        
       //get profile picture
//        token.getProfilePicture(
//                         {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq','tppMemberId':'m:9Eoc6Yda9881vKSMLTUkRViWzju:5zKtXEAq'},
//                         function(msg) {
//                         console.log("success",msg);
//                         },
//                         function(msg) {
//                         console.log("failure",msg);
//
//                         }
//                         );
//
        //delete member
//        token.deleteMember(
//                                {'memberId':'m:kzPExkVTHWxscnQNhQCzD74Gdob:5zKtXEAq'},
//                                function(msg) {
//                                console.log("success",msg);
//                                },
//                                function(msg) {
//                                console.log("failure",msg);
//
//                                }
//                                );
        
        //cancel access token
//                token.cancelAccessToken(
//                                        {'memberId':'m:2z4mMsE81YEKkq5Rk89ZUSgkYxV8:5zKtXEAq','tppMemberId':'m:ChPoyz39fJzTgH8TT7ApNiKHXeS:5zKtXEAq'},
//                                        function(msg) {
//                                        console.log("success",msg);
//                                        },
//                                        function(msg) {
//                                        console.log("failure",msg);
//
//                                        }
//                                        );
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    }
};

app.initialize();
