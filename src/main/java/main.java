import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;


@RestController
@EnableAutoConfiguration
public class main {

    public static HashMap<String, Object> props ;
    public static HashMap<String, Object> propServ ;

    int number=0;
    String nouveauport;


    //Url is : http://localhost:1234/calcul?first=12&second=1
    //Before using this functionnality, you need to start 3 servers
    //Start s1 : To position on the good repository TARGET
    //java -jar cluster-1.0-SNAPSHOT.jar 's1'
    @GetMapping(value = "/calcul")
    @ResponseBody
    String home(@RequestParam("first") int first,
                @RequestParam("second") int  second,
                //ServletRequest request,
                //ServletResponse response,
                HttpServletRequest request,
                HttpServletResponse response
    )throws IOException {

                //Random number : 0 or 1
                Random rand = new Random();
                int randomNumber = rand.nextInt(2);

                if(randomNumber==0){
                nouveauport = chooseNewPort();
                response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY); // SC_MOVED_TEMPORARILY = 302
                // DOC : Status code (302) indicating that the resource has temporarily moved to another location,
                // but that future references should still use the original URI to access the resource.

                    String requestNewUrl = request.getScheme() + "://" +
                        request.getServerName() +
                        ":" + nouveauport +
                        request.getRequestURI() +
                        (request.getQueryString() != null ? "?" + request.getQueryString() : "");

                response.setHeader("Location", requestNewUrl);
                //The goal is to reconstruct the URL with a new port and redirect
                    return response.getHeader("Location");

                }

                int result = first+second;
    return "Résultat sur le port : "+request.getServerPort()
            +" <br /> Résultat du calcul : " +result;
    }



    private String chooseNewPort(){
        //This method need to redirect and choose new port
        //If the default port is s1 so there are redirection in s2
        if(propServ.get("name").equals("s1")){
            return propServ.get("s2").toString();
        }
        //The same with s2 and s3
        else if(propServ.get("name").equals("s2")){
            return propServ.get("s3").toString();
        }
        //The same with s3 and s1
        else {
            return propServ.get("s1").toString();
        }
    }



    public static void main(String[] args) throws Exception {

        propServ = new HashMap<String, Object>();
        props = new HashMap<String, Object>();

        propServ.put("name", args[0]);
        propServ.put("s1", 1234);
        propServ.put("s2", 2345);
        propServ.put("s3", 3456);

        props.put("server.port",propServ.get(args[0]));
        System.out.print("Le serveur va se lancer sur le port : "+propServ.get(args[0]));


        new SpringApplicationBuilder()
                .sources(main.class)
                .properties(props)
                .run(args);
    }
}
