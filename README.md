# Nva Alma-SRU Proxy

The purpose of this project is to fetch an Alma-record (publication) by given parameters. One has to send the authors id
 (authorityId resp. ```scn```) and the authors name (```creatorname```) in inverted form as query parameters.
The return value is expected to be the most recent publication of the author. 

The application uses several AWS resources, including Lambda functions and an API Gateway API. These resources are 
defined in the `template.yaml` file in this project. You can update the template to add AWS resources through the same 
deployment process that updates your application code.



## Example

* GET to 

        /alma/?scn=[scn]&creatorname=[creatorname] 
        
        NB! the creatorname has to be in inverted form (e.g. "Ibsen, Henrik")
        
     Response:
     ```json
        [
          {
            "title": "Norges eldste medalje tildeles May-Britt Moser og Edvard Moser",
            "authors": [
                {"name": "Moser, May-Britt",
                 "scn": "123456789"}, 
                {"name": "Moser, Edvard",
                  "scn": "987654321"}
                ],
            "publisher": "NTNU nyheter",
            "publication_date": "2020",
            "mmsId": "112233445566778899"
          }
        ]
     ```
