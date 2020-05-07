package com.github.gb28181.entity;

import java.io.Serializable;
import java.util.Date;
import javax.sip.Dialog;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(exclude = "callId")
public class StreamInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String deviceId;
    private String ssrc;
    private String callId;
    private String channelId;
    private Date createDate;
    private String transport;
    @JsonIgnore
    private Dialog dialog;
}
