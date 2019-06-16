public class AGDController{
    ArrayList<Location> allLocationUpdates = locationDAO.getLocationInWindow(start,end);
    HashMap<String, ArrayList<LocationChunk>> macChunkMap = getMacChunksMap(allocationUpdates);
    ArrayList<Group> groups = generateGroups(macChunkMap);
    //loop all entry<mac,chunks>
    
    if(group.contiansMember){
        continue;
    }
    if(group.acceptsMember(MacChunks)){
        findNextMember(macChunk<map,group);
    }
    if(last entry<mac,chunks>){
        return;
    }
    
    //rename group from groups that have group time together < 12min
    
    //rename subset groups using group isSubSetOf();

}