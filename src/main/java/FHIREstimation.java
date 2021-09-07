

public class FHIREstimation {

    public static void main(String[] args) throws Exception {
        System.out.println(Long.MAX_VALUE);

        System.out.printf("java version: %s. Main done...", System.getProperty("java.version"));
    }

}

/*
-------------
1-st chunk
-------------
HCFHIR-33 General parameters support for FHIR api = 8 sp / 16 sp = 13 sp
HCFHIR-37 Content type negotiation for FHIR api = 3 sp / 2 sp = 3 sp
HCFHIR-38 Resource Metadata and Versioning for FHIR api = 3 sp / 5 sp = 5 sp
HCFHIR-39 Additional fields for FHIR api (date etc) = 2 sp / 1 sp = 2 sp
HCFHIR-40 Conditional read for FHIR api = 3 sp / 3 sp = 3 sp
HCFHIR-41 Create resource all left work for FHIR api = 5 sp / ??? = 5 sp
HCFHIR-42 Conditional DELETE for FHIR api = 3 sp / 2 sp = 3 sp
HCFHIR-43 Conditional UPDATE for FHIR api = 5 sp / 5 sp = 5 sp
HCFHIR-45 HEAD operation for FHIR api = 3 sp / 1 sp = 2 sp
HCFHIR-46 Transaction operation for FHIR api = 13 sp / 8 sp = 13 sp
HCFHIR-47 Batch operation for FHIR api = 8 sp / 3 sp = 5 sp
HCFHIR-48 Capabilities support for FHIR api = 5 sp / 10 sp = 8 sp
HCFHIR-49 FHIR version parameter support for FHIR api = 2 sp / 1 sp = 1 sp
HCFHIR-50 Add custom headers for FHIR api = 8 sp / 6 sp = 8 sp
HCFHIR-51 Type level create for FHIR api = 5 sp / 5 sp = 5 sp
HCFHIR-52 Handling attachments for FHIR api = 5 sp / 8 sp = 8 sp

total: 13 + 3 + 5 + 2 + 3 + 5 + 3 + 5 + 2 + 13 + 5 + 8 + 1 + 8 + 5 + 8 = 89 sp
-------------
2nd chunk
-------------
HCFHIR-55 Configure IDM for FHIR api = 3 sp / 5 sp = 5 sp
HCFHIR-56 Configure Splat inside DEV k8s cluster for FHIR api = 7 sp / 6 sp = 7 sp
HCFHIR-57 Add Annotation for RBAC processing for FHIR api = 8 sp / 8 sp = 8 sp
HCFHIR-58 Add CORS headers for FHIR api = 3 sp / 3 sp = 3 sp
HCFHIR-59 Check HSTS/XSS/CSRF/etc headers for FHIR api = 2 sp / 3 sp = 3 sp
HCFHIR-60 Configure HTTPS for FHIR api = 5 sp / 4 sp = 5 sp
HCFHIR-61 Deploy FHIR api with AuthN/Z to DEV Tier = 3 sp / 5 sp = 5 sp
HCFHIR-62 Add audit logs support for FHIR api = 12 sp / 8 sp = 12 sp
HCFHIR-65 Implement artifact sharing between code generator and fhir-api-service = 2 sp / 3 sp = 3 sp
HCFHIR-66 Implement DDL and code generator for all 146 FHIR Resources = 21 sp
HCFHIR-67 Integrate generated code into FHIR api service (all 146 mappers and DTOs) = 21 sp
HCFHIR-68 DB versioning approach for FHIR api service = 3 sp / 5 sp = 5 sp
HCFHIR-69 Connection pool sharding for FHIR api service = 13 sp / 21 sp = 21 sp
HCFHIR-70 CI pipeline for FHIR api service = 5 sp / 3 sp = 5 sp
HCFHIR-71 Deployment configuration for FHIR api service = 3 sp / 3 sp = 3 sp
HCFHIR-75 Hard DELETE for FHIR api = 3 sp / 5 sp = 5 sp
HCFHIR-77 vREAD for FHIR api = 2 sp

total: 5 + 7 + 8 + 3 + 3 + 5 + 5 + 12 + 3 + 21 + 21 + 5 + 21 + 5 + 3 + 5 + 2 = 134 sp

-------------
3rd chunk (skipped)
-------------
HCFHIR-53 Search functionality for FHIR api - 40 sp = 40 sp
HCFHIR-54 History functionality for FHIR api - 3 sp = 3 sp

total: 40 + 3 = 43 sp

-------------
GRAND total: 89 + 134 + 43 = 266 sp
266 sp + 20% (bug fixing etc) = 320 sp
-------------

HCFHIR-63 Add Security Labels for FHIR api - N/A
HCFHIR-64 Add Communications for FHIR api - N/A
 */
