package yashpc.CoreFeatures;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HistogramOfLetters {

    public static void main(String args[]){
        //you have 2 files, dictionary.txt & Book1.txt
    //1) load the two files
       Path dictionary = Paths.get("dictionary.txt");
       Path book1 = Paths.get("Book1.txt");

        try {
    //2) get the stream of words in book1.txt
            Stream<String> streamOfLines = Files.lines(book1); // Stream of lines
            Function<String, Stream<String>> stringStreamFunction =
                    line -> Pattern.compile(" ").splitAsStream(line);
            Stream<Stream<String>> streamOfStreamOfWords = streamOfLines.map(stringStreamFunction); // StreamOfStreams
            Stream<String> flattenStream = streamOfStreamOfWords.flatMap(s -> s); //Stream of words
            //System.out.println("Total number of words: " + flattenStream.count());

    //3) create an array to mark the worth of each English alphabet
            int[] letterWorth = {
               //     a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z
                      1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6
            };

    //4) create total score of each word present in Book1.txt
            Function<String, Integer> score1 =
                word -> word.chars() //IntStream - to get all characters present in a word in form of int
                        .map( l -> letterWorth[l - 'a'])
                        .sum();
            //System.out.println("Score of Hello: "+ score1.apply("hello"));

//            List<Integer> listOfScore = flattenStream.map(word -> word.toLowerCase())
//                    .map(score1)
//                    .collect(Collectors.toList());
//            listOfScore.forEach(System.out::println);

//            flattenStream.map(word -> word.toLowerCase())
//                    .sorted(Comparator.comparing(score1).reversed())
//                    .forEach(System.out::println);

//5) get the top 3 words in Book1.txt which has the max score
//            flattenStream.map(word -> word.toLowerCase())
//                    .sorted(Comparator.comparing(score1).reversed())
//                    .limit(3)
//                    .forEach(System.out::println);

//6) create another array which shows the limitation of how many times each letter could be used
            final int[] limitationsOnLetters = {
                //  a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z
                    1,2,3,4,2,1,3,3,2,0,1,2,3,4,5,6,7,1,4,0,1,2,3,4,5,6
            };
//7) for extra occurence of each letter add blank spaces

            //First find how many total number of letters do you have in a word
            Function<String, Map<Integer, Long>> totalNumberOfLettersPresent =
                    word -> word.chars() //IntStream
                            .boxed() //Stream of Integers
                            .collect(Collectors.groupingBy(l -> l,
                                    Collectors.counting()));
            System.out.println("Total number of letters in 'hello': "+totalNumberOfLettersPresent.apply("hello"));

            //first find out how many number of blanks do you need in each word
            Function<String, Long> bBlanksFun =
                    word -> totalNumberOfLettersPresent.apply(word)
                            .entrySet() //Set<Map.Entry<Integer,Long>>
                            .stream()
                            .mapToLong(entry -> {
                                return Long.max(entry.getValue() -
                                        limitationsOnLetters[entry.getKey() - 'a'],0L);
                            }).sum();

           System.out.println("Number of blanks required in 'hello: "+bBlanksFun.apply("hellllo"));

//8) modify the score function to recalculate the new score considering blank spaces

            Function<String, Integer> score2 =
                    word -> totalNumberOfLettersPresent.apply(word)
                            .entrySet()
                            .stream() //Stream of Integer
                            .mapToInt(entry -> {
                                return letterWorth[entry.getKey() - 'a']*
                                        Integer.min( entry.getValue().intValue(),
                                                limitationsOnLetters[entry.getKey() - 'a']);
                            })
                            .sum();

//9) Use NEW score function to calculate score of each word in Book1.txt
//            flattenStream.map(word -> word.toLowerCase())
//                    .map(score2)
//                           .forEach(System.out::println);

            flattenStream.map(word -> word.toLowerCase())
                    .filter(word -> score2.apply(word) > 4)
                    .sorted(Comparator.comparing(score2).reversed())
                    .forEach(System.out::println);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
