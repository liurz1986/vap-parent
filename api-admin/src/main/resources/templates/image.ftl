<div class="item">
    <h3 style="text-align: left;">${title}</h3>
    <span>${secondaryTitle}</span>
    <#if imageStr=="">
        <div style="text-align: center; line-height: 60px;">暂无数据</div>
    <#else>
        <img src="data:image/png;base64,${imageStr}"/>
    </#if>

</div>