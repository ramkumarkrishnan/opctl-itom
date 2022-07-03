# Worksheet for OpCTL ITOM Project

This worksheet helps you to track the various OCI resources you will
be creating as part of the project, so that you can audit their usage,
clean them in the right order without leaving dangling resources, and
create Terraform scripts for automated deployment.

### A. Setup your tenancy
- __User__: *v_user_name*; OCID: ocid1.user.oc1..unique-string
- __Tenancy__: *v_tenancy_name*. Get this from (OCI Console -> user icon
  in the navbar). OCID: ocid1.tenancy.oc1..unique-string
- __Region__: *v_region_name* (Example: US East (Ashburn)).
- __Region Identifier__: *v_region_identifier* (Example: us-ashburn-1;
You can obtain it from the OCI Console HTTPS address after you login:
`https://cloud.oracle.com/tenancy?region=us-ashburn-1`)
- __Region Key__: *v_region_key* (Example: 'iad' for us-ashburn-1).
  Get this from [here](https://docs.oracle.com/en-us/iaas/Content/General/Concepts/regions.htm)
- __Root Compartment__: This will be same as *v_tenancy_name*;
  OCID: ocid1.tenancy.oc1..unique-string


- __Group__: *v_group_name*. OCID: ocid1.group.oc1..unique_string
- __Compartment__: *v_compartment_name*.
- __Compartment_OCID__: *v_compartment_ocid* of the form
ocid1.compartment.oc1..unique-string.


- __VCN__: *v_vcn_name*.
  OCID: ocid1.vcn.oc1.*v_region_key*.unique_string
- __Public Subnet__: Public Subnet-*your_vcn_name*. OCID:
  ocid1.subnet.oc1.*v_region_key*.unique_string
- __Security List for Public Subnet__: Default Security List for
  *your_vcn_name*.
  OCID: ocid1.securitylist.oc1.*v_region_key*.unique_string
- __Security List Ingress and Egress Rules__: Note down the tables.
- __Route table for Public Subnet__: Default Route Table for *your_vcn_name*.
  OCID: ocid1.routetable.oc1.*v_region_key*.unique_string
- __Internet Gateway__: Internet Gateway-*your_vcn_name*.
  OCID: ocid1.internetgateway.oc1.*v_region_key*.unique_string
- __Private Subnet__:
- __Security List for Private Subnet__: Security List for Private Subnet-
  *your_vcn_name*.
  OCID: ocid1.securitylist.oc1.*v_region_key*.unique_string
- __Security List Ingress and Egress Rules__:
- __Route table for Private Subnet__: Route Table for Private Subnet-
  *your_vcn_name*. OCID:
  ocid1.routetable.oc1.*v_region_key*.unique_string
- __NAT Gateway__: NAT Gateway-*your_vcn_name*.
  OCID: ocid1.natgateway.oc1.*v_region_key*.unique_string
- __Service Gateway__: Service Gateway-*your_vcn_name*.
  OCID: ocid1.servicegateway.oc1.*v_region_key*.unique_string


- __Policy__: *v_policy_name*. OCID: ocid1.policy.oc1..unique_string

### B. Create Application
- __Application__: *v_application_name*. OCID:
  ocid1.fnapp.oc1.*v_region_key*.unique_string
- __Application Logs__: *v_application_name*_invoke. OCID:
  ocid1.log.oc1.*v_region_key*.unique_string
- __APM domain__: *v_apm_domain_name*. OCID:
  ocid1.apmdomain.oc1.*v_region_key*.unique_string

### C. Set up local host dev environment
- __Docker__: No ID - recording as an inventory item. You can optionally
  register at hub.docker.com with username/password for access to the hub.
- __User Auth Token: No name - description "auth token to enable docker
  container in local host login to OCI Container Registry".
  OCID: ocid1.credential.oc1..unique_string
- __User API Keys__: Fingerprint with alphanumeric pattern
  aa:bb:cc:dd:ee:ff:gg:hh:ii:jj:kk:ll:mm:nn:oo:pp.
- __Config File__: ~/.oci/config with entries from configuration file template
  provided with API Keys.


- __OCI CLI__: No ID - recording as an inventory item. Install CLI.
- __Fn Project CLI__: No ID - recording as an inventory item. Install Fn CLI.
- __Fn Project CLI Context__: *your_fn_cli_context_name*
- __OCI Registry Repo Name Prefix__: *v_registry_repo_name_prefix*