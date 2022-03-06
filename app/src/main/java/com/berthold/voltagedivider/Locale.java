package com.berthold.voltagedivider;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class serves as a storage for procedural generated strings
 * inside the various view- model classes where we have no access
 * on the string resources of the application and therefore
 * it is impossible to localize the text inside these strings.
 *
 * Each Activity or Fragment creates an instance of this class,
 * gets the strings from the resources and stores them
 * here using the dedicated setter methods.
 *
 * An instance of this class is then passed by the activity/ fragment to it's view model
 * from where we can obtain the localized strings at
 * runtime....
 *
 * All strings used in all view models must be stored here.
 *
 * ToDo: Find a way to make this accessible directly by the view models.....
 *
 */
public class Locale {

    public String protocolStartText,noSolutionFoundText,searchingText,showingText;

    public void setNoSolutionFoundText(String noSolutionFoundText){
        this.noSolutionFoundText=noSolutionFoundText;
    }
    public String getNoSolutionFound() {
        return noSolutionFoundText;
    }

    public void setProtocolStartText(String protocolStartText) {
        this.protocolStartText = protocolStartText;
    }

    public String getProtocolStartText() {
        return protocolStartText;
    }

    public String getSearchingText() {
        return searchingText;
    }

    public void setSearchingText(String searchingText) {
        this.searchingText = searchingText;
    }

    public String getShowingText() {
        return showingText;
    }

    public void setShowingText(String showingText) {
        this.showingText = showingText;
    }
}
