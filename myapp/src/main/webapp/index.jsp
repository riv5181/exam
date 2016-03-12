<%-- 
    Document   : index
    Created on : Mar 12, 2016, 10:21:49 AM
    Author     : Raguel
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="Connector.ObjectStorageConnector" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>index</title>
    </head>
    <body>
        <form action = "processUpload" enctype="multipart/form-data">
            File: <input type = "file" name = "uploadFile">
            <input type="submit" value="Upload wav">
        </form>
        
        <% 
            ObjectStorageConnector connect = new ObjectStorageConnector();
            if(connect.listAllObjects("exam").isEmpty())
            {
                connect.createContainer("exam"); 
            }    
        %>
    </body>
</html>
