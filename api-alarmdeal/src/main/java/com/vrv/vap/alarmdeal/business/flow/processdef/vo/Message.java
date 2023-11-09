package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@ApiModel(value="消息定义")
public class Message {
    /**
     * 唯一标识
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Integer id;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    @ApiModelProperty(value="推送给用户的ID,说明：推送对象为用户时必传，为用户组时，可不传")
    private Integer userId;

    /**
     * 0：系统公告，1：应用提示
     */
    @ApiModelProperty(hidden = true)
    private Byte source;

    /**
     * 消息标题
     */
    @ApiModelProperty(value="消息标题",required = true)
    private String title;

    /**
     * 消息内容,支持HTML语法格式（说明：目前没有做防XSS）
     */
    @ApiModelProperty(value="消息内容,支持HTML语法格式",required = true)
    private String content;

    /**
     * 消息可以跳转到的链接地址
     */
    @ApiModelProperty(value="详情链接地址，如果此参数不为空，则消息会有一个查看详情的按钮跳转到此链接")
    private String url;

    /**
     * 来自用户ID
     */
    @Column(name = "from_id")
    @ApiModelProperty(hidden = true)
    private Integer fromId;

    /**
     * 0：未读，1：已读
     */
    @ApiModelProperty(hidden = true)
    private Byte status;

    @Column(name = "send_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty(hidden = true)
    private Date sendtime;

    @Column(name = "read_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty(hidden = true)
    private Date readtime;


    @Column(name = "send_batch")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty(hidden = true)
    private String sendbatch;

    /**
     * 获取唯一标识
     *
     * @return id - 唯一标识
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置唯一标识
     *
     * @param id 唯一标识
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取用户ID
     *
     * @return user_id - 用户ID
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取0：系统公告，1：应用提示
     *
     * @return source - 0：系统公告，1：应用提示
     */
    public Byte getSource() {
        return source;
    }

    /**
     * 设置0：系统公告，1：应用提示
     *
     * @param source 0：系统公告，1：应用提示
     */
    public void setSource(Byte source) {
        this.source = source;
    }

    /**
     * 获取消息标题
     *
     * @return title - 消息标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置消息标题
     *
     * @param title 消息标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取消息内容,支持HTML语法格式（说明：目前没有做防XSS）
     *
     * @return content - 消息内容,支持HTML语法格式（说明：目前没有做防XSS）
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置消息内容,支持HTML语法格式（说明：目前没有做防XSS）
     *
     * @param content 消息内容,支持HTML语法格式（说明：目前没有做防XSS）
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取消息可以跳转到的链接地址
     *
     * @return url - 消息可以跳转到的链接地址
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置消息可以跳转到的链接地址
     *
     * @param url 消息可以跳转到的链接地址
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取0：未读，1：已读
     *
     * @return status - 0：未读，1：已读
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置0：未读，1：已读
     *
     * @param status 0：未读，1：已读
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Date getSendtime() {
        return sendtime;
    }

    public void setSendtime(Date sendtime) {
        this.sendtime = sendtime;
    }

    public Date getReadtime() {
        return readtime;
    }

    public void setReadtime(Date readtime) {
        this.readtime = readtime;
    }


    public String getSendbatch() {
        return sendbatch;
    }

    public void setSendbatch(String sendbatch) {
        this.sendbatch = sendbatch;
    }
}