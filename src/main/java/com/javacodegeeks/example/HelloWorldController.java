package com.javacodegeeks.example;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.ApiException;
import com.docusign.esign.model.Document;
import com.docusign.esign.model.EnvelopeDefinition;
import com.docusign.esign.model.EnvelopeSummary;
import com.docusign.esign.model.RecipientViewRequest;
import com.docusign.esign.model.Recipients;
import com.docusign.esign.model.SignHere;
import com.docusign.esign.model.Signer;
import com.docusign.esign.model.Tabs;
import com.docusign.esign.model.Text;
import com.docusign.esign.model.ViewUrl;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

@RestController
public class HelloWorldController {

    @GetMapping("/hello")
    public String sayHello() throws IOException, ApiException {

        return doThings();
    }

    private static String doThings() throws ApiException, IOException {


        // Data for this example
        // Fill in these constants
        //
        // Obtain an OAuth access token from https://developers.docusign.com/oauth-token-generator
        String accessToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImtpZCI6IjY4MTg1ZmYxLTRlNTEtNGNlOS1hZjFjLTY4OTgxMjIwMzMxNyJ9.eyJUb2tlblR5cGUiOjUsIklzc3VlSW5zdGFudCI6MTU3MzA2MTE1MiwiZXhwIjoxNTczMDg5OTUyLCJVc2VySWQiOiIzZjZiYmQ2YS04ZmIzLTQzZmMtOWEwZi04YjU0MzhiODA1OTUiLCJzaXRlaWQiOjEsInNjcCI6WyJzaWduYXR1cmUiLCJjbGljay5tYW5hZ2UiLCJvcmdhbml6YXRpb25fcmVhZCIsImdyb3VwX3JlYWQiLCJwZXJtaXNzaW9uX3JlYWQiLCJ1c2VyX3JlYWQiLCJ1c2VyX3dyaXRlIiwiYWNjb3VudF9yZWFkIiwiZG9tYWluX3JlYWQiLCJpZGVudGl0eV9wcm92aWRlcl9yZWFkIiwiZHRyLnJvb21zLnJlYWQiLCJkdHIucm9vbXMud3JpdGUiLCJkdHIuZG9jdW1lbnRzLnJlYWQiLCJkdHIuZG9jdW1lbnRzLndyaXRlIiwiZHRyLnByb2ZpbGUucmVhZCIsImR0ci5wcm9maWxlLndyaXRlIiwiZHRyLmNvbXBhbnkucmVhZCIsImR0ci5jb21wYW55LndyaXRlIl0sImF1ZCI6ImYwZjI3ZjBlLTg1N2QtNGE3MS1hNGRhLTMyY2VjYWUzYTk3OCIsImlzcyI6Imh0dHBzOi8vYWNjb3VudC1kLmRvY3VzaWduLmNvbS8iLCJzdWIiOiIzZjZiYmQ2YS04ZmIzLTQzZmMtOWEwZi04YjU0MzhiODA1OTUiLCJhbXIiOlsiaW50ZXJhY3RpdmUiXSwiYXV0aF90aW1lIjoxNTczMDYxMTQ5LCJwd2lkIjoiODFmZTU5OGYtZmIxZC00ZGU0LTkwN2UtNjVlMDkxMmNhNjg0In0.39gxZGPYSf_3-vTocwSRplchqt9dbd_ZM1ocq9zK-1lfE-Y7rSXqsJ3bHfTGhbLI9A48uOcdCMQTF9mxgv4eI5CEBPvSReXsawv4Bng0ccflKPiRsBd0JJ2hJ6uV5wsnSYTgDYWgpCrI2EY7HsDGTrfzsvzqVuSO5sr0UQesfZzrSMzlnSTCreKfe49HhFzBtaNSP9hw5NrlDza1HuWO0j9Q4U-nRkLNJoXLner8EEfdbzhvtnIEsViK_WYGFPfkld-1JRvcUcZzwLEwgKGQJtfHpw_0VxMApBjuSMObSJViSiN5rU_GoZVTx9JZs7soX9dPjEiXT2IfPVYR1FlVQg";
        // Obtain your accountId from demo.docusign.com -- the account id is shown in the drop down on the
        // upper right corner of the screen by your picture or the default picture.
        String accountId = "9249784";
        // Recipient Information
        String signerName = "attendee";
        String signerEmail = "amity@cvent.com";


        // The url for this web application
        String baseUrl = "http://localhost:8082";
        String clientUserId = "3f6bbd6a-8fb3-43fc-9a0f-8b5438b80595"; // Used to indicate that the signer will use an embedded
        // Signing Ceremony. Represents the signer's userId within
        // your application.
        String authenticationMethod = "None"; // How is this application authenticating
        // the signer? See the `authenticationMethod' definition
        //  https://developers.docusign.com/esign-rest-api/reference/Envelopes/EnvelopeViews/createRecipient
        //
        // The API base path
        String basePath = "https://demo.docusign.net/restapi";
        // The document to be signed. See /qs-java/src/main/resources/World_Wide_Corp_lorem.pdf
        String docPdf = "World_Wide_Corp_lorem.pdf";
        //String docPdf = "CVENT_DIGITAL_SIGN.docx";
        //String docPdf = "CVENT_DIGITAL.pdf";
        // Step 1. Create the envelope definition
        // One "sign here" tab will be added to the document.

        byte[] buffer = readFile(docPdf);
        String docBase64 = new String(Base64.encode(buffer));

        // Create the DocuSign document object
        Document document = new Document();
        document.setDocumentBase64(docBase64);
        document.setName("Example document"); // can be different from actual file name
        document.setFileExtension("docx"); // many different document types are accepted
        document.setDocumentId("1"); // a label used to reference the doc

        // The signer object
        // Create a signer recipient to sign the document, identified by name and email
        // We set the clientUserId to enable embedded signing for the recipient
        Signer signer = new Signer();
        signer.setEmail(signerEmail);
        signer.setName(signerName);
        signer.clientUserId(clientUserId);
        signer.recipientId("1");

        // Create a signHere tabs (also known as a field) on the document,
        // We're using x/y positioning. Anchor string positioning can also be used
        SignHere signHere = new SignHere();
        signHere.setDocumentId("1");
        signHere.setPageNumber("1");
        signHere.setRecipientId("1");
        signHere.setTabLabel("SignHereTab");
        signHere.setXPosition("195");
        signHere.setYPosition("147");

//        Text email = new Text();
//        email.setDocumentId("1");
//        email.setPageNumber("1");
//        signHere.setRecipientId("1");
//        signHere.setTabLabel("EmailText");
//        signHere.setXPosition("295");
//        signHere.setYPosition("247");
//
//
//        Text name = new Text();
//        name.setDocumentId("1");
//        name.setPageNumber("1");
//        name.setRecipientId("1");
//        name.setTabLabel("NameText");
//        name.setXPosition("395");
//        name.setYPosition("347");

        //List<Text> texts = Arrays.asList(email, name);

        // Add the tabs to the signer object
        // The Tabs object wants arrays of the different field/tab types
        Tabs signerTabs = new Tabs();
        signerTabs.setSignHereTabs(Arrays.asList(signHere));
        //signerTabs.setTextTabs(texts);
        signer.setTabs(signerTabs);

        // Next, create the top level envelope definition and populate it.
        EnvelopeDefinition envelopeDefinition = new EnvelopeDefinition();
        envelopeDefinition.setEmailSubject("Please sign this document");
        envelopeDefinition.setDocuments(Arrays.asList(document));
        // Add the recipient to the envelope object
        Recipients recipients = new Recipients();
        recipients.setSigners(Arrays.asList(signer));
        envelopeDefinition.setRecipients(recipients);
        envelopeDefinition.setStatus("sent"); // requests that the envelope be created and sent.
        // Step 2. Call DocuSign to create and send the envelope
        ApiClient apiClient = new ApiClient(basePath);
        apiClient.addDefaultHeader("Authorization", "Bearer " + accessToken);
        EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
        EnvelopeSummary results = envelopesApi.createEnvelope(accountId, envelopeDefinition);
        String envelopeId = results.getEnvelopeId();

        // Step 3. The envelope has been created.
        //         Request a Recipient View URL (the Signing Ceremony URL)
        RecipientViewRequest viewRequest = new RecipientViewRequest();
        // Set the url where you want the recipient to go once they are done signing
        // should typically be a callback route somewhere in your app.
        viewRequest.setReturnUrl(baseUrl + "/ds-return");
        viewRequest.setAuthenticationMethod(authenticationMethod);
        viewRequest.setEmail(signerEmail);
        viewRequest.setUserName(signerName);
        viewRequest.setClientUserId(clientUserId);
        // call the CreateRecipientView API
        ViewUrl results1 = envelopesApi.createRecipientView(accountId, envelopeId, viewRequest);

        // Step 4. The Recipient View URL (the Signing Ceremony URL) has been received.
        //         The user's browser will be redirected to it.
        String redirectUrl = results1.getUrl();
//        RedirectView redirect = new RedirectView(redirectUrl);
//        redirect.setExposeModelAttributes(false);
        return redirectUrl;

    }


    protected static byte[] readFile(String path) throws IOException {
        InputStream is = HelloWorldController.class.getResourceAsStream("/" + path);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;

        byte[] data = new byte[1024];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

}
