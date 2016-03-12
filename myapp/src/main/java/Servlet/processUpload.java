/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlet;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import org.apache.commons.io.IOUtils;

import org.openstack4j.model.common.Payload;
import org.openstack4j.model.common.Payloads;

import com.ibm.watson.developer_cloud.speech_to_text.v1.SpeechToText;
import com.ibm.watson.developer_cloud.speech_to_text.v1.model.SpeechResults;

import java.util.List;

import Connector.ObjectStorageConnector;
import Connector.SpeechtoTextConnector;

/**
 *
 * @author Raguel
 */

@WebServlet(name = "processUpload", urlPatterns = {"/processUpload"})
public class processUpload extends HttpServlet 
{

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) 
        {
            ObjectStorageConnector connect = new ObjectStorageConnector();
            SpeechtoTextConnector connector = new SpeechtoTextConnector();
            SpeechToText service = new SpeechToText();
            service.setUsernameAndPassword(connector.getUsername(),connector.getPassword());
			
            String filename = null;
            Payload upfile = null;

            if (ServletFileUpload.isMultipartContent(request)) 
            {

                FileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);

                //Uploads file to object storage
                try 
                {
                    List<FileItem> fields = upload.parseRequest(request);
                    Iterator<FileItem> it = fields.iterator();
                    List<FileItem> multiparts = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
                    
                    while (it.hasNext()) 
                    {
                        FileItem fileItem = it.next();
                        boolean isFormField = fileItem.isFormField();
                        
                        if (isFormField) 
                        {
                            
                        }
                        
                        else 
                        {
                            filename = fileItem.getName();
                            upfile = Payloads.create(fileItem.getInputStream());
                        }
                    }

                    if (!filename.isEmpty() && !(upfile == null)) 
                    {
                        connect.uploadFile("exam", filename, upfile);
                    }
                    
                    for(FileItem item : multiparts)
                    {
                        if(!item.isFormField())
                        {
                            InputStream in = item.getInputStream();
                            String PREFIX = "temporary";
                            String SUFFIX = ".wav";
                            
                            File tempFile = File.createTempFile(PREFIX, SUFFIX);
                            FileOutputStream out1 = new FileOutputStream(tempFile);
                            IOUtils.copy(in, out1);
                            
                            File audio = tempFile;
                            
                            SpeechResults output = service.recognize(audio, "audio/wav");
                            String print = output.toString();
                            
                            request.setAttribute("message", print);     
    
                        }
                    }
                } 
                
                catch (Exception e) 
                {
                    
                }
                
            }
            
            response.sendRedirect("convert.jsp");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
