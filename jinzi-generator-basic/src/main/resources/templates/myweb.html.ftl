<!doctype html>
<html lang="en">
<head>
     <title>jizni</title>
</head>
<body>
    <h1>
        欢迎来到Jinzi官网
    </h1>
    <ul>
        <#list menuItems as item>
            <li><a href="${item.url}">${item.label}</a> </li>
        </#list>
    </ul>
<footer>
    ${currentYear} jinzi官网 all rights reserved.
</footer>

</body>
</html>