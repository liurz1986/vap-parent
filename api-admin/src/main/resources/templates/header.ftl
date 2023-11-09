<!DOCTYPE html>
<html lang="zh-CN">
<head>
  	<meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>${title}</title>
    <style>
        html,body{
            width: 100%;
            margin: 0 auto;
            font-family: simhei;
        }
        h1{
            text-align: center;
            margin-top: 48px;
            font-size: 48px;
            font-weight: bold;
        }
        h2{
            text-align: center;
            margin-top: 24px;
            font-size: 24px;
            font-weight: normal;
            font-style: italic;
        }
        .summary{
            background-color: #eee;
            color: #333333;
            padding: 2px 20px;
        }
        .summary p{
            line-height: 1.5;
        }
        .summary p i{
            padding:0 3px;
            color: #000000;
        }
        .summary p strong{
            padding:0 3px;
            color: #DE4923;

        }
        .root{
            width: 720px;
            margin: 0 auto;
        }
        .item{
            border-top: 1px dashed #cccccc;
            margin-bottom: 12px;
        }
        .item h3{
            text-align: center;
            font-size: 18px;
            font-weight: bold;
        }
        .item table{
            border: none;
            width: 100%;
            text-align: left;
            border-collapse: separate;
            border-spacing: 0;
        }
        .item table thead{
            background-color: #EEFAF4;
        }
        .item table thead tr{
            border-bottom: 1px solid #e8e8e8;
        }
        .item table thead tr th{
            padding: 10px 6px;
            background-color: transparent;
            font-size: 14px;
            color: #333333;
            font-weight: 500;
            text-align: left;
            border:none;
            border-bottom: 1px solid #CADBD3;
            border-right: 1px solid #CADBD3;
        }
        .item table thead tr th:last-child{
            border-right:   none;
        }
        .item table tbody{

        }
        .item table tr{
            border:none;
            background-color: transparent;
        }
        .item table tr:nth-child(2n) {
            background: #f7f7f7;
        }
        .item table tr td{
            padding: 6px 6px;
            font-size: 13px;
            border: none;
            border-bottom: 1px solid #e8e8e8;
            border-right:   1px solid #e8e8e8;
        }

        .item table tr td:last-child{
            border-right:   none;
        }

        .item img{
            text-align: center;
            display: block;
            max-width: 680px;
            margin: 0 auto;
            border: 1px solid #e8e8e8;
            padding: 6px;
        }
    </style>
</head>
<body>
<div class="root">
	<h1>${title}</h1>
	<h3 style="text-align: center;">${subtitle}</h3>