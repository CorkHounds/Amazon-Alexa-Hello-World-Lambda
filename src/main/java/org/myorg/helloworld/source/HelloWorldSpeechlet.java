/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package org.myorg.helloworld.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.Directive;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.speechlet.dialog.directives.DelegateDirective;
import com.amazon.speech.speechlet.dialog.directives.DialogIntent;
import com.amazon.speech.speechlet.dialog.directives.DialogSlot;
import com.amazon.speech.speechlet.IntentRequest.DialogState;

/**
 * This sample shows how to create a simple speechlet for handling speechlet requests.
 */
public class HelloWorldSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(HelloWorldSpeechlet.class);

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return getWelcomeResponse();
    }

    @SuppressWarnings("rawtypes")
	@Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        //Get Dialog State
        DialogState dialogueState = request.getDialogState();

        if ("HelloWorld".equals(intentName)) {

            //If the IntentRequest dialog state is STARTED and you accept Utterances that
        	//allow a user to provide slots?  If not, you don't need to return the updatedIntent.
            if (dialogueState.equals(DialogState.STARTED)) {
	        	
            	//Create a new DialogIntent
		        DialogIntent dialogIntent = new DialogIntent();
		        
		        //Set the name to match our intentName
		        dialogIntent.setName(intentName);
		        
		        //Map over the Dialog Slots
		        //We do this to ensure that we include any slots already provided by the user
		        Map<String,DialogSlot> dialogSlots = new HashMap<String,DialogSlot>();
		        
		        //Set up an iterator
		        Iterator iter = intent.getSlots().entrySet().iterator();
		        
	            log.debug("Building DialogIntent");
	            //Iterate and copy over all slots/values
		        while (iter.hasNext()) {
		        	    	
		            Map.Entry pair = (Map.Entry)iter.next();
		            
		            //Create a new DialogSlot
		            DialogSlot dialogSlot = new DialogSlot();
		            
		            //Create a new Slot
		            Slot slot = (Slot) pair.getValue();
		            
		            //Set the name of the slot
		            dialogSlot.setName(slot.getName());
		            
		            //Copy over the value if its already set
		            if (slot.getValue() != null)
		            	dialogSlot.setValue(slot.getValue());
		            
		            //Add this DialogSlot to the DialogSlots Hashmap.
		            dialogSlots.put((String) pair.getKey(), dialogSlot);
		            
		            log.debug("DialogSlot " + (String) pair.getKey() + " with Name " + slot.getName() + " added.");
		        }
		        	    
		        //Set the dialogSlots on the DialogIntent
		        dialogIntent.setSlots(dialogSlots);
		        	    
		        //Create a DelegateDirective
		        DelegateDirective dd = new DelegateDirective();
		        
		        //Add our new DialogIntent to the DelegateDirective
		        dd.setUpdatedIntent(dialogIntent);
		        	    
		        //Directives must be provided as a List.  Add our DelegateDirective to the List.
		        List<Directive> directiveList = new ArrayList<Directive>();
		        directiveList.add(dd);
	
		        //Create a new SpeechletResponse and set the Directives to our List.
		        SpeechletResponse speechletResp = new SpeechletResponse();
		        speechletResp.setDirectives(directiveList);
		        
		        //Only end the session if we have all the info. Assuming we still need to 
		        //get more, we keep the session open.
		        speechletResp.setShouldEndSession(false);
		        
		        //Return the SpeechletResponse.
		        return speechletResp;
	        		
            } else if (dialogueState.equals(DialogState.COMPLETED)) {

            	log.debug("onIntent, inside dialogueState IF statement");
            	//Generate our response and return.
            	return getHelloResponse(intent);
	        	
		    } else { // dialogueState.equals(DialogState.IN_PROGRESS)
		        		
		        log.debug("onIntent, inside dialogueState ELSE statement");
		        
		        //Create an empty DelegateDirective
		        //This will tell the Alexa Engine to keep collecting information.
		        DelegateDirective dd = new DelegateDirective();
		        
		        //Directives must be provided as a List.  Add our DelegateDirective to the List.
		        List<Directive> directiveList = new ArrayList<Directive>();
		        directiveList.add(dd);
		        
		        //Create a new SpeechletResponse and set the Directives to our List.
	            SpeechletResponse speechletResp = new SpeechletResponse();
		        speechletResp.setDirectives(directiveList);
		        
		        //Only end the session if we have all the info. Assuming we still need to 
		        //get more, we keep the session open.
		        speechletResp.setShouldEndSession(false); 
		        
		        //Return the SpeechletResponse.
		        return speechletResp;
            }
        } else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        } else {
            throw new SpeechletException("Invalid Intent");
        }
    }
    
    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any cleanup logic goes here
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse() {
        String speechText = "Welcome to the Alexa Skills Kit, you can say hello";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

    /**
     * Creates a {@code SpeechletResponse} for the hello intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelloResponse(Intent intent) {
        
        String name = intent.getSlot("Name").getValue();
        String speechText = "Hello, " + name;

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        return SpeechletResponse.newTellResponse(speech, card);
    }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "You can say hello to me!";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("HelloWorld");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }
}
