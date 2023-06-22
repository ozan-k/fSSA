package com.ozank.lexerParser;

import com.ozank.simulator.Reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelBuilder {

    private final List<Reaction> reactions = new ArrayList<>();
    private final Map<String,Integer> initialState = new HashMap<>();
    private double endTime;

    public ModelBuilder(String file) {
        file  = file.replaceAll("\\s*","");
        String regex = "reactions(.*)initialstate(.*)endtime(.+?);.*";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(file);
        if (m.find( )) {

        }
        parseReactions(m.group(1));
        parseInitialState(m.group(2));
        try {
            endTime = Double.parseDouble(m.group(3));
        } catch (IndexOutOfBoundsException e){
            System.out.println(e);
        }
        for (Reaction r : reactions){
            System.out.println(r);
        }
        for (String s : initialState.keySet()){
            System.out.println(s + " : " + initialState.get(s));
        }
        System.out.println("");
        System.out.println(endTime);
    }

    private void parseReactions(String reactionsString){
        String regex = "(.*):(.*)->(.*):(.*)";
        Pattern pattern = Pattern.compile(regex);
        for (String reaction: reactionsString.split(";")){
            Matcher m = pattern.matcher(reaction);
            if (m.find( )) {
                String id = m.group(1);
                Map<String,Integer> left = organizeReactants(m.group(2));
                Map<String,Integer> right = organizeReactants(m.group(3));
                double rate = Double.parseDouble(m.group(4));
                reactions.add(new Reaction(id,rate,left,right));
            }
        }
    }

    private Map<String,Integer> organizeReactants(String reactants){
        Map<String,Integer> result = new HashMap<>();
        if (reactants.equals("")){
            return result;
        }
        for (String s : reactants.split("\\+")){
            result.put(s,result.getOrDefault(s,0)+1);
        }
        return result;
    }

    private void parseInitialState(String initialStateString){
        String regex = "(.*):(.*)";
        Pattern pattern = Pattern.compile(regex);
        for (String string : initialStateString.split(";")){
            Matcher m = pattern.matcher(string);
            if (m.find( )) {
                String molecule = m.group(1);
                Integer count;
                try {
                    count = Integer.parseInt(m.group(2));
                } catch (Exception e){
                    throw new IllegalArgumentException();
                }
                if (initialState.containsKey(molecule)){
                    throw new IllegalArgumentException();
                } else {
                    initialState.put(molecule,count);
                }
            }
        }
    }

    public List<Reaction> getReactions(){
        return reactions;
    }

    public Map<String,Integer> getInitialState(){
        return initialState;
    }

    public double getEndTime(){
        return endTime;
    }

}
