package com.cmpt276.parentapp.model;

import java.util.ArrayList;
import java.util.List;

public class ChildManager {
    private List<Child> children = new ArrayList<>();

    private static ChildManager instance;
    private ChildManager(){}

    public static ChildManager getInstance(){
        if(instance == null){
            instance = new ChildManager();
        }
        return instance;
    }

    public void addKid(Child child){
        children.add(child);
    }

    public void removeChild(int index){
        children.remove(index);
    }

    public Child retrieveChildByIndex(int index){
        return children.get(index);
    }

    public List<Child> getAllChildren(){
        return children;
    }

    public int size(){
        return children.size();
    }
}
