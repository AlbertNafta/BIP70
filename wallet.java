// setup a wallet
NetworkParameters params = TestNet3Params.get();
WalletAppKit kit = new WalletAppKit(params, new File("."), "walletappkit-example"); 
kit.startAsync();
kit.awaitRunning();


String url = "https://example.com/invoice/42"; //  bitcoin:1LCBEVPm4BpHb89Vv6LKSNE1gaPSsJe7YL?amount=1.42&r=https://example.com/invoice/42

ListenableFuture<PaymentSession> future;

if (url.startsWith("http")) { /
  future = PaymentSession.createFromUrl(url);
} else if (url.startsWith("bitcoin:")) {
  future = PaymentSession.createFromBitcoinUri(new BitcoinURI(url)); // getting the payment request URL from bitcoin:..?r=URL 
}

PaymentSession session = future.get(); 

String memoFromMerchant = session.getMemo(); 
Coin amountToPay = session.getValue(); // the amount you have to pay
PaymentProtocol.PkiVerificationData identity = session.verifyPki(); // botcoinj verifies the request. The merchant has to sign the payment request using a certificate signed from a from the wallet's computer "trusted" root authority
boolean isVerified = identity != null;

System.out.println("Memo: " + memoFromMerchant);
System.out.println("Amount: " + amountToPay.toFriendlyString());
System.out.println("Date: " + session.getDate());
if(isVerified) {
  System.out.println("Verification:");
  System.out.println("Name: " + identity.displayName); // only when the payment request is verified we can display the name to whom we are paying to
  System.out.println("verified by: " + identity.rootAuthorityName); 
}

if (session.isExpired()) {
  System.out.println("request is expired!");
} else {
  
  
  
  Wallet.SendRequest req = session.getSendRequest(); // get a SendRequest creatin transactions that fulfill the payment request
  kit.wallet().completeTx(req); 
  
  String refundAddress = "mjhr9mQqCNpuzcjjFRq71MbUBA9Dv8SoPV"; 
  String customerMemo = "thanks for your service"; 
  
  ListenableFuture<PaymentProtocol.Ack> paymentFuture = session.sendPayment(req.tx, refundAddress, customerMemo);
  if(future != null) { 
    PaymentProtocol.Ack ack = future.get(); 
    kit.wallet().commitTx(req.tx); // 
    System.out.println("Ack memo from server: " + ack.getMemo()); // 
  } else {
    Wallet.SendResult sendResult = new Wallet.SendResult();
    sendResult.tx = req.tx;
    sendResult.broadcast = kit.peerGroup().broadcastTransaction(req.tx);
    sendResult.broadcastComplete = sendResult.broadcast.future();
  }
}

