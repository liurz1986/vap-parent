<#macro admin  version="">
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<meta http-equiv="X-UA-Compatible" wecontent="ie=edge">
<title>VAP管理平台</title>
<link href="/css/theme-admin.css" rel="stylesheet">
<link href="/css/iconfont.css" rel="stylesheet">
<link href="/css/admin.css" rel="stylesheet">
<script src="/js/references.admin.js"></script>
<#-- Admin 环境包含以了下JS
<script src="/js/react.min.js"></script>
<script src="/js/react-dom.min.js"></script>
<script src="/js/redux.min.js"></script>
<script src="/js/react-redux.min.js"></script>
<script src="/js/dva.min.js"></script>
<script src="/js/nprogress.js"></script>
<script src="/js/lodash.min.js"></script>
<script src="/js/moment.min.js"></script>
<script src="/js/moment-zh-cn.js"></script>
<script src="/js/antd.min.js"></script>
-->
</head>
<body version="${version}">
<div id="root"></div>
<script src="/js/admin.js"></script>
<#nested >
</body>
</html>
</#macro>


<#macro page title="" includeCss=[] version="">
<!DOCTYPE html>
<html>
<head>
    <#assign theme = __theme!'default'>
    <#assign themes = ['default',  'blue']>
    <#assign theme = themes?seq_contains(theme)?string(theme,"default")>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${title}</title>
    <link href="/css/theme-${theme}.css" rel="stylesheet">
    <link href="/css/iconfont.css" rel="stylesheet">
    <link href="/css/vap.css" rel="stylesheet">
    <#list includeCss as css><link href="${css}" rel="stylesheet"></#list>
    <script src="/js/references.display.js"></script>
<#-- Display 环境包含以了下JS
<script src="/js/react.min.js"></script>
<script src="/js/react-dom.min.js"></script>
<script src="/js/nprogress.js"></script>
<script src="/js/lodash.min.js"></script>
<script src="/js/moment.min.js"></script>
<script src="/js/moment-zh-cn.js"></script>
<script src="/js/antd.min.js"></script>
<script src="/js/echarts.min.js"></script>
<script src="/js/d3.min.js"></script>
-->
</head>
<body class="vap-${theme}" version="${version}">
<div id="__header"></div>
<script src="/js/header.js"></script><#nested >
</body>
</html>
</#macro>


<#macro screen title="" version="" includeCss=[]>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${title}</title>
    <link href="/css/theme-screen.css" rel="stylesheet">
    <link href="/css/vap.css" rel="stylesheet">
    <link href="/css/iconfont.css" rel="stylesheet">
    <#list includeCss as css><link href="${css}" rel="stylesheet"></#list>
    <script src="/js/references.screen.js"></script>
<#-- Screen 环境包含以了下JS
    <script src="/js/react.min.js"></script>
    <script src="/js/react-dom.min.js"></script>
    <script src="/js/nprogress.js"></script>
    <script src="/js/lodash.min.js"></script>
    <script src="/js/moment.min.js"></script>
    <script src="/js/moment-zh-cn.js"></script>
    <script src="/js/antd.min.js"></script>
    <script src="/js/jquery.min.js"></script>
    <script src="/js/echarts.min.js"></script>
    <script src="/js/d3.min.js"></script>
    <script src="/js/three.min.js"></script>
-->
</head>
<body class="vap-screen" version="${version}"><#nested ></body>
</html>
</#macro>