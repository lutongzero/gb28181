package com.github.gb28181.gb;

import javax.sip.RequestEvent;
import javax.sip.message.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import com.github.gb28181.sip.RequestHandler;
import com.github.gb28181.xmlbean.CatalogResp;
import com.thoughtworks.xstream.XStream;

/**
 * uac 推送catalog后，信息存储到本地
 **/
@Order(2)
@Component
public class CatalogRequestHandler implements RequestHandler {
    @Autowired
    private XStream xstream;
    @Autowired
    private CommonStoreService storeService;

    @Override
    public void processRequest(RequestEvent evt) {
        xstream.toXML(new CatalogResp());
        CatalogResp catalog =
                (CatalogResp) xstream.fromXML(new String(evt.getRequest().getRawContent()));
        storeService.saveCatalog(catalog);
    }

    @Override
    public boolean isSupport(Request req) {
        String method = req.getMethod();
        String content = null;
        byte[] bytes = req.getRawContent();
        if (bytes != null) {
            content = new String();
        }
        return Request.MESSAGE.equals(method) && content != null && content.contains("CmdType")
                && content.contains("Catalog");
    }

}
