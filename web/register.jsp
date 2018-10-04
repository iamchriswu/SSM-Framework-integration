<%--
  Created by IntelliJ IDEA.
  User: Chris
  Date: 2018/9/7
  Time: 15:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>用户注册</title>
</head>
<body>
<form action="${pageContext.request.contextPath}register.action">
    <table border="1">
        <tr>
            <td>用户名</td>
            <td><input type="text" name="userName"></td>
        </tr>
        <tr>
            <td>密码</td>
            <td><input type="text" name="password"></td>
        </tr>
        <tr>
            <td><input type="submit" value="注册"></td>
        </tr>
    </table>
</form>
</body>
</html>
