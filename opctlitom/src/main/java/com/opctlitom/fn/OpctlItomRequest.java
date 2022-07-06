/**
 * OpctlItomRequest.java - This program implements an OCI Function that
 * translates an OpCTL approval request to a ServiceNow incident authenticates
 * connection to a ServiceNow REST endpoint using secret stored in OCI Vault,
 * and POSTs the incident in the Incidents table in ServiceNow ITOM service.
 *
 * AUTHOR   DATE        DETAILS
 * ramkrish 06/17/2022  Modify handler to translate incident data
 * ramkrish 06/16/2022  Creation
 * 45678901234567890123456789012345678901234567890123456789012345678901234567890
 */
package com.opctlitom.fn;

import com.fnproject.fn.api.InputEvent;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class OpctlItomRequest {
    private String secretOCID = System.getenv("OPCTLITOM_SECRET_OCID");
    private SecretsClient secretsClient;
    public OpctlItomRequest() {
        BasicAuthenticationDetailsProvider provider = null;
        String version = System.getenv("OCI_RESOURCE_PRINCIPAL_VERSION");
        if( version != null ) { // if the function runs in Fn Server in cloud
            provider = ResourcePrincipalAuthenticationDetailsProvider.builder().build();
        }
        else {  // if the function runs in Fn Server on the local host
            try {
                provider = new ConfigFileAuthenticationDetailsProvider(
                    "/Users/ramkrish/.oci/config", "DEFAULT");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Provider: " + provider);
        secretsClient = new SecretsClient(provider);
        secretsClient.setRegion(Region.US_ASHBURN_1);
    }

    public String handleRequest(InputEvent rawInput) {
        try {
            URL url = new URL(
    "https://dev111332.service-now.com/api/now/table/incident?sysparm_limit=1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");

            // String userCredentials = "admin:P0/Bw-6emNvK"; // hard-coded
            String userCredentials = getSecretValue(secretOCID); // from Vault
            String secretValueDecodedString =
                Base64.encodeBase64String(userCredentials.getBytes());

            String auth = "Basic " + secretValueDecodedString;
            conn.setRequestProperty("Authorization", auth);

            String inputEventJson = rawInput.consumeBody(this::readData);
            String data = getItomJson(inputEventJson);

            // For Fn Invoke from Local Host - TEST
            if (data == null) {
                data = "{\"short_description\":\"Fn Invoke from Cmd Line\"}";
            }
            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream str = conn.getOutputStream();
            str.write(out);

            String err = "HTTP code:"
                + conn.getResponseCode() + ":"
                + conn.getResponseMessage()
                + ": SecretsClient: " +  secretsClient.toString()
                + ": SecretsOCID: " + secretOCID
                + ": AUTH: " + auth
                + ": INPUT:" + inputEventJson
                + ": TS: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"))
                + ": OUTPUT: " + data;
            if (conn.getResponseCode() != 201)
                throw new RuntimeException("FAILED:" + err);
            else
                System.out.println("SUCCESS: " + err);
            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "0";
    }

    private String getSecretValue(String secretOCID) throws IOException {
        GetSecretBundleRequest getSecretBundleRequest =
            GetSecretBundleRequest
            .builder()
            .secretId(secretOCID)
            .stage(GetSecretBundleRequest.Stage.Current)
            .build();

        GetSecretBundleResponse getSecretBundleResponse =
            secretsClient.getSecretBundle(getSecretBundleRequest);

        Base64SecretBundleContentDetails base64SecretBundleContentDetails =
            (Base64SecretBundleContentDetails)
            getSecretBundleResponse
            .getSecretBundle()
            .getSecretBundleContent();

        byte[] secretValueDecoded =
            Base64.decodeBase64(base64SecretBundleContentDetails.getContent());

        return new String(secretValueDecoded);
    }

    private String readData(InputStream s) {
        return new BufferedReader(new InputStreamReader(s))
            .lines().collect(Collectors.joining(" / "));
    }

    private String getItomJson(String inputJson) {
        JsonElement jsonElement = JsonParser.parseString(inputJson);
        if (!jsonElement.isJsonObject()) {
            return null;
        }

        JsonObject eventObject = jsonElement.getAsJsonObject();
        SnowIncident snowIncident = new SnowIncident();

        snowIncident.setActive("true");
        snowIncident.setOpenedBy(eventObject.get("source").getAsString());
        snowIncident.setOpenedAt(eventObject.get("eventTime").getAsString());
        snowIncident.setDueDate(eventObject.get("eventTime").getAsString());
        snowIncident.setCompany("Oracle Corporation"); // NO OPCTL EVENT I/P
        snowIncident.setAssignedTo("Customer Name");   // NO OPCTL EVENT I/P
        snowIncident.setSeverity(String.valueOf(10));  // NO OPCTL EVENT I/P
        snowIncident.setImpact("Low");   // NO OPCTL EVENT INPUT
        snowIncident.setPriority("Low"); // NO OPCTL EVENT INPUT
        snowIncident.setUrgency("Low");  // NO OPCTL EVENT INPUT
        snowIncident.setState("Active"); // NO OPCTL EVENT INPUT
        snowIncident.setLocation("us-ashburn-1"); // SAMPLE - NO OPCTL I/P
        snowIncident.setFollowUp("None"); // NO OPCTL EVENT INPUT
        snowIncident.setComments("None"); // NO OPCTL EVENT INPUT

        JsonObject dataObject = eventObject.get("data").getAsJsonObject();
        JsonObject aDtlsObject =
            dataObject.get("additionalDetails").getAsJsonObject();

        String operation = eventObject.get("eventType").getAsString();

        String shortDescription =
            "{"
            + "Operation:" + operation;
        if (operation.contains("accessrequest")) {
            shortDescription +=
          ",accessRequestId:" + aDtlsObject.get("accessRequestId").getAsString()
          + ",Request_URL:" + aDtlsObject.get("accessRequest_url").getAsString()
          + ",ReasonSummary:" + aDtlsObject.get("reasonSummary").getAsString();
        }
        else if (operation.contains("createoperatorcontrol"))  {
            shortDescription +=
            ",operatorcontrol_ocid:"
                + aDtlsObject.get("operatorcontrol_ocid").getAsString()
            + ",operatorcontrol_name:"
                + aDtlsObject.get("operatorcontrol_name").getAsString();
        }
        shortDescription += "}";
        snowIncident.setShortDescription(shortDescription);

        String description =
            "{";
        if (operation.contains("accessrequest")) {
            description +=
                "eventID:" + eventObject.get("eventID").getAsString()
                + ",compartmentId:"
                    + dataObject.get("compartmentId").getAsString()
                + ",compartmentName:"
                    + dataObject.get("compartmentName").getAsString()
                + ",resourceId:" + dataObject.get("resourceId").getAsString()
                + ",resourceName:"
                    + dataObject.get("resourceName").getAsString()
                + ",availabilityDomain:"
                    + dataObject.get("availabilityDomain").getAsString()
                + ",exadatainfrastructure_ocid:"
                + aDtlsObject.get("exadatainfrastructure_ocid").getAsString()
                + ",exadatainfrastructure_name:"
                + aDtlsObject.get("exadatainfrastructure_name").getAsString()
                + ",accessRequestId:"
                    + aDtlsObject.get("accessRequestId").getAsString()
                + ",opCtlId:" + aDtlsObject.get("opCtlId").getAsString()
                + ",opCtlName:" + aDtlsObject.get("opCtlName").getAsString()
                + ",reason:" + aDtlsObject.get("reason").getAsString();
        }
        else if (operation.contains("createoperatorcontrol"))  {
            description += "description:" + "DUMMY DESCRIPTION at TS:" +
                LocalDateTime.now()
                .format(DateTimeFormatter
                .ofPattern("MM-dd-yyyy HH:mm:ss"));
        }
        description += "}";
        snowIncident.setDescription(description);

        Gson gson = new Gson();
        String snowJson = gson.toJson(snowIncident);
        return snowJson;
    }
}
//345678901234567890123456789012345678901234567890123456789012345678901234567890
