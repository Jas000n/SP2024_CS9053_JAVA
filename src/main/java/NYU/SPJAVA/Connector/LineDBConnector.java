package NYU.SPJAVA.Connector;

import NYU.SPJAVA.DBEntity.Line;
import NYU.SPJAVA.utils.Response;

import java.util.ArrayList;

public class LineDBConnector {
    // save all lines
    // passing in a arraylist of lines, with every fields populated except line_id
    // return a boolean should be fine, but to keep things uniform, we return a response
    public Response saveLines(ArrayList<Line>lines){
        return null;
    }
    //retrieve all lines from a picture
    //passing in a picture id
    //return all lines encapsulated in an array, Lines[], sorted in timestamp
    public Response getLines(int picture_id){
        return null;
    }
}
