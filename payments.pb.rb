
require 'protocol_buffers'
module Payments
  # forward declarations
  class Output < ::ProtocolBuffers::Message; end
  class PaymentDetails < ::ProtocolBuffers::Message; end
  class PaymentRequest < ::ProtocolBuffers::Message; end
  class X509Certificates < ::ProtocolBuffers::Message; end
  class Payment < ::ProtocolBuffers::Message; end
  class PaymentACK < ::ProtocolBuffers::Message; end

  class Output < ::ProtocolBuffers::Message
    set_fully_qualified_name "payments.Output"

    optional :uint64, :amount, 1, :default => 0
    required :bytes, :script, 2
  end

  class PaymentDetails < ::ProtocolBuffers::Message
    set_fully_qualified_name "payments.PaymentDetails"

    optional :string, :network, 1, :default => "main"
    repeated ::Payments::Output, :outputs, 2
    required :uint64, :time, 3
    optional :uint64, :expires, 4
    optional :string, :memo, 5
    optional :string, :payment_url, 6
    optional :bytes, :merchant_data, 7
  end

  class PaymentRequest < ::ProtocolBuffers::Message
    set_fully_qualified_name "payments.PaymentRequest"

    optional :uint32, :payment_details_version, 1, :default => 1
    optional :string, :pki_type, 2, :default => "none"
    optional :bytes, :pki_data, 3
    required :bytes, :serialized_payment_details, 4
    optional :bytes, :signature, 5
  end

  class X509Certificates < ::ProtocolBuffers::Message
    set_fully_qualified_name "payments.X509Certificates"

    repeated :bytes, :certificate, 1
  end

  class Payment < ::ProtocolBuffers::Message
    set_fully_qualified_name "payments.Payment"

    optional :bytes, :merchant_data, 1
    repeated :bytes, :transactions, 2
    repeated ::Payments::Output, :refund_to, 3
    optional :string, :memo, 4
  end

  class PaymentACK < ::ProtocolBuffers::Message
    set_fully_qualified_name "payments.PaymentACK"

    required ::Payments::Payment, :payment, 1
    optional :string, :memo, 2
  end

end

