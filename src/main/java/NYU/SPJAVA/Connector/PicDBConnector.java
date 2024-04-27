package NYU.SPJAVA.Connector;

import NYU.SPJAVA.DBEntity.Picture;
import NYU.SPJAVA.utils.Response;

public class PicDBConnector {
    //save a picture to DB, passing in a picture object with every field except: picture id and title
    // (as i havent figure out in which precess we have the player set the title of the picture)
    //return a pic object with every field populated
    public Response addPic(Picture pic){
        return null;
    }

    //update a pic, according to picture id, pass in a pic with other field populated(but dont necessarily have to be a
    // completed populated pic object
    //return a bool should be enough
    public Response update(Picture pic){
        return null;
    }

}
