package com.github.gb28181.gb;

import javax.sip.Dialog;
import javax.sip.InvalidArgumentException;
import javax.sip.ResponseEvent;
import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.github.gb28181.entity.StreamInfo;
import com.github.gb28181.sip.ResponseHandler;

/**
 * 
 * */
@Order(1)
@Component
public class AckResponseHandler implements ResponseHandler {
    @Autowired
    private CommonStoreService storeService;

    @Override
    public void handlerResponse(ResponseEvent evt) throws InvalidArgumentException, SipException {
        Dialog dialog = evt.getDialog();
        storeService.saveDialog(dialog.getCallId().getCallId(), dialog);
        Request reqAck = dialog.createAck(1L);
        dialog.sendAck(reqAck);
    }

    @Override
    public boolean isSupport(ResponseEvent evt) {
        return evt.getResponse().getStatusCode() == 200 && isStream(evt);
    }

    private boolean isStream(ResponseEvent evt) {
        CallIdHeader header = (CallIdHeader) evt.getResponse().getHeader(CallIdHeader.NAME);
        StreamInfo streamInfo = storeService.getStreamInfo(header.getCallId());
        return streamInfo != null;

    }
}
