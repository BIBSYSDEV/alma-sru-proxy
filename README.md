# Alma-SRU Proxy

The purpose of this project is to fetch an Alma-record (publication) by given parameters. One has to
send the authors id (authorityId resp. ```scn```) and the authors name (```creatorname```) in 
inverted form as query parameters.
The return value is expected to be the title of the most recent publication of the author. 

A second endpoint will get an Alma-record by given ```mms_id``` from NETWORK_ZONE. The return value 
is expected to be the Alma-record in xml format. Adding a second parameter ```institution``` will 
try to add local information from the given institution. The institution-parameter is expected to be
in alma-code (e.g. NB, NTNU_UB, UBO). Instead of ```mms_id``` (possibly with ```institution```) one 
can provide only ```isbn``` as parameter. The lambda will then return the corresponding records 
(the records with the given isbn in field 020$a).

A third endpoint is able to retrieve holdings-data to a given ```mms_id``` (```recordSchema=isohold```)
and response from sru is filtered by given ```library_code```. 

The application uses several AWS resources, including Lambda functions and an API Gateway API. These resources are 
defined in the `template.yaml` file in this project. You can update the template to add AWS resources through the same 
deployment process that updates your application code.

Prerequisites (shared resources):
* HostedZone: [sandbox|dev|test|prod].bibs.aws.unit.no
* Create a CodeStarConnection that allows CodePipeline to get events from and read the GitHub repository

  The user creating the connection must have permission to create "apps" i GitHub
* SSM Parameter Store Parameters:
  * /api/domainName = api.[sandbox|dev|test|prod].bibs.aws.unit.no
  * /github-connection = (CodeStarConnections ARN from above)
* Create the following CloudFormation stack manually using the AWS Web Console, CLI or API:
  * Stack for Custom Domain Name, Certificate and Route53 RecordSet:
    * Template: api-domain-name.yaml
    * Name: apigw-custom-domain-name-api-[sandbox|dev|test|prod]-bibs-aws-unit-no
    * Parameters:
      * HostedZoneId=[ID]

Bootstrap:
* Create the following CloudFormation stack manually using the AWS Web Console, CLI or API:
  * Stack for pipeline/CICD. This will bootstrap the app stack (template.yml)
    * Template: pipeline.yml
    * Name: alma-sru-proxy-cicd
    * Parameters:
      * DeployStackName=alma-sru-proxy
      * GitBranch=develop
      * GitRepo=BIBSYSDEV/alma-sru-proxy
      * PipelineApprovalAction=[Yes|No] (No for non-prod?)
      * (Optional) PipelineApprovalEmail=[email address]


## Example

* GET to 

        /alma/?scn=[scn]&creatorname=[creatorname] 
        
        NB! the creatorname has to be in inverted form (e.g. "Ibsen, Henrik")
        
     Response:
     ```json
          {
            "title": "Norges eldste medalje tildeles May-Britt Moser og Edvard Moser"
          }
     ```
  
* GET to 

        /alma/?mms_id=[mms_id]&instituition=[NB]
        
        NB! the mms_id from NETWORK_ZONE is expected. 
        
     Response:
     ```xml
          <?xml version="1.0" encoding="UTF-8"?>
          <record xmlns="http://www.loc.gov/MARC21/slim">
              <leader>
                  01044cam a2200301 c 4500
              </leader>
              <controlfield tag="001">
                  991325803064702201
              </controlfield>
              <controlfield tag="005">
                  20160622160726.0
              </controlfield>
              <controlfield tag="007">
                  ta
              </controlfield>
              <controlfield tag="008">
                  141124s2013    no#||||j||||||000|0|nob|^
              </controlfield>
              <datafield ind1=" " ind2=" " tag="015">
                  <subfield code="a">
                      1337755
                  </subfield>
                  <subfield code="2">
                      nbf
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="020">
                  <subfield code="a">
                      9788210053412
                  </subfield>
                  <subfield code="q">
                      ib.
                  </subfield>
                  <subfield code="c">
                      Nkr 249.00
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="035">
                  <subfield code="a">
                      132580306-47bibsys_network
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="035">
                  <subfield code="a">
                      (NO-TrBIB)132580306
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="035">
                  <subfield code="a">
                      (NO-OsBA)0370957
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="040">
                  <subfield code="a">
                      NO-OsNB
                  </subfield>
                  <subfield code="b">
                      nob
                  </subfield>
                  <subfield code="e">
                      katreg
                  </subfield>
              </datafield>
              <datafield ind1="1" ind2=" " tag="041">
                  <subfield code="h">
                      eng
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="042">
                  <subfield code="a">
                      norbibl
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="044">
                  <subfield code="c">
                      no
                  </subfield>
              </datafield>
              <datafield ind1="7" ind2="4" tag="082">
                  <subfield code="a">
                      791.4372
                  </subfield>
                  <subfield code="q">
                      NO-OsNB
                  </subfield>
                  <subfield code="2">
                      5/nor
                  </subfield>
              </datafield>
              <datafield ind1="1" ind2=" " tag="100">
                  <subfield code="a">
                      Fisher, Jude
                  </subfield>
                  <subfield code="0">
                      (NO-TrBIB)1093967
                  </subfield>
              </datafield>
              <datafield ind1="1" ind2="0" tag="245">
                  <subfield code="a">
                      Hobbiten :
                  </subfield>
                  <subfield code="b">
                      Smaugs ødemark i bilder
                  </subfield>
                  <subfield code="c">
                      Jude Fisher ; oversatt fra engelsk av Camilla Eikeland-Sandnes
                  </subfield>
              </datafield>
              <datafield ind1="1" ind2=" " tag="246">
                  <subfield code="a">
                      The Hobbit
                  </subfield>
                  <subfield code="b">
                      the desolation of Smaug visual companion
                  </subfield>
                  <subfield code="i">
                      Originaltittel
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="260">
                  <subfield code="a">
                      Oslo
                  </subfield>
                  <subfield code="b">
                      Tiden
                  </subfield>
                  <subfield code="c">
                      2013
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="300">
                  <subfield code="a">
                      75 s.
                  </subfield>
                  <subfield code="b">
                      ill.
                  </subfield>
                  <subfield code="c">
                      28 cm
                  </subfield>
              </datafield>
              <datafield ind1="1" ind2=" " tag="700">
                  <subfield code="a">
                      Eikeland-Sundnes, Camilla
                  </subfield>
                  <subfield code="d">
                      1978-
                  </subfield>
                  <subfield code="4">
                      trl
                  </subfield>
                  <subfield code="0">
                      (NO-TrBIB)10061339
                  </subfield>
              </datafield>
              <datafield ind1="4" ind2="2" tag="856">
                  <subfield code="3">
                      Beskrivelse fra forlaget (kort)
                  </subfield>
                  <subfield code="u">
                      http://content.bibsys.no/content/?type=descr_publ_brief&amp;isbn=8210053418
                  </subfield>
              </datafield>
              <datafield ind1="0" ind2="1" tag="852">
                  <subfield code="a">
                      47BIBSYS_NB
                  </subfield>
                  <subfield code="6">
                      991325803064702202
                  </subfield>
                  <subfield code="9">
                      D
                  </subfield>
                  <subfield code="9">
                      P
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="901">
                  <subfield code="a">
                      90
                  </subfield>
              </datafield>
              <datafield ind1=" " ind2=" " tag="913">
                  <subfield code="a">
                      Norbok
                  </subfield>
                  <subfield code="b">
                      NB
                  </subfield>
              </datafield>
          </record>
     ```

  * GET to

          /alma/?mms_id=[mms_id]&instituition=[NB]&recordSchema=isohold&libraryCode=[libraryCode]
        
          NB! the mms_id from NETWORK_ZONE is expected. 

    Response:
       ```xml
    <?xml version="1.0" encoding="UTF-8"?><searchRetrieveResponse xmlns="http://www.loc.gov/zing/srw/">
        <version>1.2</version>
        <numberOfRecords>1</numberOfRecords>
        <records>
            <record>
                <recordSchema>isohold</recordSchema>
                <recordPacking>xml</recordPacking>
                <recordData>
                    <holdings xmlns="http://www.loc.gov/standards/iso20775/">
                        <holding>
                            <institutionIdentifier>
                                <value>NO-1042300</value>
                                <typeOrSource>
                                    <text>ISIL</text>
                                </typeOrSource>
                            </institutionIdentifier>
                            <physicalLocation>Norsk Skogfinsk Museum</physicalLocation>
                            <holdingSimple>
                                <copiesSummary>
                                    <copiesCount>2</copiesCount>
                                    <status>
                                        <availableCount>0</availableCount>
                                        <earliestDispatchDate>2019-12-19T00:00:00.000Z</earliestDispatchDate>
                                    </status>
                                </copiesSummary>
                                <copyInformation>
                                    <pieceIdentifier>
                                        <value>barcode15</value>
                                        <typeOrSource>
                                            <text>ITEM ID</text>
                                        </typeOrSource>
                                    </pieceIdentifier>
                                    <resourceIdentifier>
                                        <value>2238439990002286</value>
                                        <typeOrSource>
                                            <text>HOLDING MMS ID</text>
                                        </typeOrSource>
                                    </resourceIdentifier>
                                    <sublocation>wl0001</sublocation>
                                    <availabilityInformation>
                                        <status>
                                            <availabilityStatus>2</availabilityStatus>
                                        </status>
                                        <policy>3</policy>
                                    </availabilityInformation>
                                </copyInformation>
                                <copyInformation>
                                    <pieceIdentifier>
                                        <value>barcode14</value>
                                        <typeOrSource>
                                            <text>ITEM ID</text>
                                        </typeOrSource>
                                    </pieceIdentifier>
                                    <resourceIdentifier>
                                        <value>2238439990002286</value>
                                        <typeOrSource>
                                            <text>HOLDING MMS ID</text>
                                        </typeOrSource>
                                    </resourceIdentifier>
                                    <sublocation>wl0001</sublocation>
                                    <availabilityInformation>
                                        <status>
                                            <availabilityStatus>2</availabilityStatus>
                                            <dateTimeAvailable>2019-12-19T00:00:00.000Z</dateTimeAvailable>
                                        </status>
                                    </availabilityInformation>
                                </copyInformation>
                            </holdingSimple>
                        </holding>
                        <resource>
                            <resourceIdentifier>
                                <value>999919767594702286</value>
                                <typeOrSource>
                                    <text>SUFFICIENT</text>
                                </typeOrSource>
                            </resourceIdentifier>
                        </resource>
                    </holdings>
                </recordData>
                <recordIdentifier>999919767594702286</recordIdentifier>
                <recordPosition>1</recordPosition>
            </record>
        </records>
        <extraResponseData xmlns:xb="http://www.exlibris.com/repository/search/xmlbeans/">
            <xb:exact>true</xb:exact>
            <xb:responseDate>2019-12-19T10:34:06+0100</xb:responseDate>
        </extraResponseData>
    </searchRetrieveResponse>

     ```