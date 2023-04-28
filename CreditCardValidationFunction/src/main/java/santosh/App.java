package santosh;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.apache.commons.lang3.StringUtils;
import santosh.models.CreditCardValidationResult;

/**
 * Handler for requests to Lambda function.
 */

/**
 * Better way to handle
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        if( input != null ) {
            Map<String, String> inputParams = input.getQueryStringParameters();
            if(inputParams != null ) {
                CreditCardValidationResult creditCardValidationResult = Optional.ofNullable(
                                inputParams.entrySet())
                        .get()
                        .stream()
                        .filter(entry -> entry.getKey().equals("number"))
                        .findFirst()
                        .map(this::validCreditCardNumber).orElse(null);

                if (creditCardValidationResult != null) {
                    Gson gson = new Gson();
                    String json = gson.toJson(creditCardValidationResult);
                    return response
                            .withStatusCode(200)
                            .withBody(json);
                } else {
                    return response
                            .withBody("{}")
                            .withStatusCode(403); // Invalid input
                }
            }
        }
    String output = String.format("{ \"message\": \"Missing Input CreditCard Number\", \"time\": \"%s\" }", System.currentTimeMillis());
    return response
            .withBody(output)
            .withStatusCode(403); // Invalid input

    }

    private CreditCardValidationResult validCreditCardNumber(Map.Entry<String,String> entry){
        String creditCardNumber = entry.getValue();
        if(StringUtils.isNumeric(creditCardNumber)){
            if( creditCardNumber.length() == 16 ){
                    if(creditCardNumber.startsWith("4")){
                        return new CreditCardValidationResult(true,CreditCardTypes.VISA);
                    }else if(creditCardNumber.startsWith("51") ||
                            creditCardNumber.startsWith("52") ||
                            creditCardNumber.startsWith("53") ||
                            creditCardNumber.startsWith("54") ||
                            creditCardNumber.startsWith("55")){
                        return new CreditCardValidationResult(true, CreditCardTypes.MASTER);
                    }else if(creditCardNumber.startsWith("6011") || creditCardNumber.startsWith("65")){
                        return new CreditCardValidationResult(true,CreditCardTypes.DISCOVER);
                    }
            }else if(creditCardNumber.length() == 12 && creditCardNumber.startsWith("4")){
                return new CreditCardValidationResult(true,CreditCardTypes.VISA);
            }
        }
        return null ;
    }
}
