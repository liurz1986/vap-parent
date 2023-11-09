<div class="item">
    <h3 style="text-align: left;">${title}</h3>
    <span>${secondaryTitle}</span>
    <table>
        <thead>
        <tr>
            <#list headList as item>
                <th>${item}</th>
            </#list>
        </tr>
        </thead>
        <tbody>
        <#list dataList as item>
            <tr>
                <td>${item.id}</td>
                <td>${item.content}</td>
                <td>${item.count}</td>
                <td>${item.count2}</td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>