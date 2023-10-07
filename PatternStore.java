package uk.ac.cam.ld558.oop.tick3;

import uk.ac.cam.ld558.oop.tick1.Pattern;

import java.io.*;
import java.net.*;
import java.util.*;
/*
Stores a list of patterns (i.e. compatible string formats that initialise the Game of Life board) that can be
selected from the GUI when the game is simulated.
 */
public class PatternStore {
    private List<Pattern> patterns = new LinkedList<>();
    private Map<String,List<Pattern>> mapAuths = new HashMap<>();
    private Map<String,Pattern> mapName = new HashMap<>();

    public PatternStore(String source) throws IOException{
        //If the source of GOL patterns derives from an online source then load appropiately.
        if (source.startsWith("http://") || source.startsWith("https://")) {
            try {
                loadFromURL(source);
            } catch (PatternFormatException e) {
                e.printStackTrace();
            }
        }
        //Otherwise load from local storage.
        else {
            try {
                loadFromDisk(source);
            } catch (PatternFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public PatternStore(Reader source) throws IOException, PatternFormatException {
        load(source);
    }

    private void load(Reader r) throws IOException, PatternFormatException {
        /**
         * Takes each line from the reader, hopefully in the desired pattern format for a game
         * configuration and loads it into the necessary mappings and collections for convenient ordered
         * display in the GUI.
         */
        //Read each line from the reader which provides the assortment of game configurations and
        //store in the associated collections.
        BufferedReader b = new BufferedReader(r);
        //The line currently being read by the reader.
        String line;
        //A temporary storage variable for pattern.
        Pattern mediatepattern;
        while ((line = b.readLine()) != null) {
            try {
                mediatepattern = new Pattern(line);
                //If the pattern is not already contained in the collection store then add it.
                if(!patterns.contains(mediatepattern)){
                    patterns.add(mediatepattern);
                    //Add the author to the corresponding authorial collection likewise.
                    String patauthor = mediatepattern.getAuthor();
                    if (mapAuths.containsKey(patauthor)) {
                        //If a pattern is present already attributable to the author for the pattern in question,
                        //then add this pattern under the repositories belonging to that author and update
                        //the collection accordingly.
                        List<Pattern> updlist = mapAuths.get(patauthor);
                        updlist.add(mediatepattern);
                        mapAuths.put(patauthor, updlist);
                    } else {
                        //Otherwise, both pattern and author are new and add to the collections as new entitites.
                        List<Pattern> authpatlist = new LinkedList<>();
                        authpatlist.add(mediatepattern);
                        mapAuths.put(patauthor, authpatlist);
                    }
                    //Add all patterns under a mapping of name to pattern.
                    mapName.put(mediatepattern.getName(), mediatepattern);
                }
            }
            catch(PatternFormatException e){
                System.out.println(line);
                continue;
                }

            }
        }
    private void loadFromURL(String url) throws IOException, PatternFormatException {
        //Creates a Reader for the URL and then call load on it
        URL destination = new URL(url);
        URLConnection conn = destination.openConnection();
        Reader r = new InputStreamReader(conn.getInputStream());
        load(r);
    }

    private void loadFromDisk(String filename) throws IOException, PatternFormatException {
        //Creates a Reader for the file and then call load on it
        Reader r = new FileReader(filename);
        load(r);
    }

    public List<Pattern> getPatternsNameSorted() {
        //Forms a list of all the patterns provided from the resource and sorts by name of the pattern,
        //in alphabetical order.
        List<Pattern> namesortpatterns = new LinkedList<>();
        namesortpatterns = patterns;
        Collections.sort(namesortpatterns);
        return new ArrayList<Pattern>(namesortpatterns);
    }

    public List<Pattern> getPatternsAuthorSorted() {
        //Forms a list of all the patterns provided from the resource and sorts by name of the author and
        //then by the name of the pattern as a tiebreaker.
        List<Pattern> authsortpatterns = new LinkedList<>();
        authsortpatterns = patterns;
        Collections.sort(authsortpatterns, new Comparator<Pattern>() {
            @Override
            //Override the compare method to implement the desired tiebreaker of compare by pattern name.
            public int compare(Pattern o1, Pattern o2) {
                if(o1.getAuthor().compareTo(o2.getAuthor())==0){
                    return (o1.getName().compareTo(o2.getName()));
                }
                else{
                    return (o1.getAuthor().compareTo(o2.getAuthor()));
                }
            }
        });
        return new ArrayList<Pattern>(authsortpatterns);
    }

    public List<Pattern> getPatternsByAuthor(String author) throws PatternNotFound {
        // Return a list of patterns from a particular author sorted by name
        List<Pattern> authorpatternattribution = mapAuths.get(author);
        if(authorpatternattribution==null){
            throw new PatternNotFound();
        }
        Collections.sort(authorpatternattribution);
        return new ArrayList<Pattern>(authorpatternattribution);
    }

    public Pattern getPatternByName(String name) throws PatternNotFound {
        // TGet a particular pattern by name
        if(mapName.get(name)==null){
            throw new PatternNotFound();
        }
        return mapName.get(name);
    }

    public List<String> getPatternAuthors() {
        // Get a sorted list of all pattern authors in the store
        List<String> patternAuthors = new LinkedList<>();
        mapAuths.forEach((k,v)-> patternAuthors.add(k));
        Collections.sort(patternAuthors);
        return patternAuthors;
    }


    public List<String> getPatternNames() {
        // Gets a list of all pattern names in the store,
        // sorted by name
        List<String> patternNames = new LinkedList<>();
        mapName.forEach((k,v)-> patternNames.add(k));
        Collections.sort(patternNames);
        return patternNames;
    }

    public static void main(String args[]) throws IOException, PatternFormatException, PatternNotFound {
        //Initalises the pattern store with whatever resource contrains the patterns in desired format,
        //be that a URL or local file.
        PatternStore p = new PatternStore(args[0]);
        p.getPatternsAuthorSorted().forEach(s -> System.out.println(s.getName().toString()));
    }
}

