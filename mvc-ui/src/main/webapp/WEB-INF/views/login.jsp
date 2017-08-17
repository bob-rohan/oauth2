<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
</head>
<body>
	<c:url var="login" value="/loginProcess" />
	<form action="${login}" method="POST">
		<input type="text" id="username" name="username" />
		<input type="password" id="password" name="password" />
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		<button type="submit" class="btn">Log in</button>
	</form>
</body>
</html>