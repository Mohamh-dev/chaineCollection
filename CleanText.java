package tpChaineCollection;
import java.util.*;

public class CleanText {
    
    static String[] RAW_COMMENTS = {
        "Java est génial! J'adore Java...",
        "java, JAVA, JaVa — trop de versions ?",
        "Les listes en Java sont puissantes; les Sets aussi.",
        "Map<Map, Map> ? Non merci ; mais les Map simples oui.",
        "java & python: amour/haine, mais Java reste top."
    };
    
    static String STOPWORDS = "est,les,la,le,de,des,en,et,mais,oui,non,trop";
 
    public static String normalize(String s, StringBuffer buffer) {
        buffer.setLength(0);
        String result = s.toLowerCase();
        result = result.replaceAll("[^a-zàâçéèêëîïôûùüÿñæœ0-9]", " ");
        result = result.replaceAll("\\s+", " ");
        result = result.strip();
        buffer.append(result);
        return result;
    }
    
    public static List<String> tokens(String s) {
        StringBuffer buffer = new StringBuffer();
        String normalized = normalize(s, buffer);
        if (normalized.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(normalized.split(" "));
    }
    
    public static String reconstruct(List<String> tokens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.size(); i++) {
            sb.append(tokens.get(i));
            if (i < tokens.size() - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    
   public static List<String> filterStopwords(List<String> toks, Set<String> stopset) {
        List<String> filtered = new ArrayList<>();
        for (String token : toks) {
            if (!stopset.contains(token)) {
                filtered.add(token);
            }
        }
        return filtered;
    }
    
    public static List<String> getAllTokens(String[] comments, Set<String> stopset) {
        List<String> allTokens = new ArrayList<>();
        for (String comment : comments) {
            List<String> toks = tokens(comment);
            List<String> filtered = filterStopwords(toks, stopset);
            allTokens.addAll(filtered);
        }
        return allTokens;
    }
    
    
    public static Map<String, Integer> wordFrequencies(List<String> tokens) {
        Map<String, Integer> freq = new HashMap<>();
        for (String token : tokens) {
            freq.put(token, freq.getOrDefault(token, 0) + 1);
        }
        return freq;
    }
    
    public static List<Map.Entry<String, Integer>> topK(Map<String, Integer> freq, int k) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(freq.entrySet());
        
        entries.sort((e1, e2) -> {
            int cmpValue = e2.getValue().compareTo(e1.getValue());
            if (cmpValue != 0) return cmpValue;
            return e1.getKey().compareTo(e2.getKey());
        });
        
        return entries.subList(0, Math.min(k, entries.size()));
    }
    
    public static Map<String, Set<Integer>> buildInvertedIndex(String[] comments, Set<String> stopset) {
        Map<String, Set<Integer>> index = new HashMap<>();
        
        for (int i = 0; i < comments.length; i++) {
            List<String> toks = tokens(comments[i]);
            List<String> filtered = filterStopwords(toks, stopset);
            Set<String> uniqueWords = new HashSet<>(filtered);
            
            for (String word : uniqueWords) {
                index.putIfAbsent(word, new TreeSet<>()); // TreeSet pour ordre croissant
                index.get(word).add(i);
            }
        }
        
        return index;
    }
    public static void main(String[] args) {
        Set<String> stopset = new HashSet<>(Arrays.asList(STOPWORDS.split(",")));
        System.out.println("Normalisation:");
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < RAW_COMMENTS.length; i++) {
            System.out.println("  Original: " + RAW_COMMENTS[i]);
            String normalized = normalize(RAW_COMMENTS[i], buffer);
            System.out.println("  Normalisé: " + normalized);
            System.out.println("  StringBuffer: " + buffer.toString());
            System.out.println();
        }
        
        System.out.println("Découpage & reconstruction:");
        String sample = RAW_COMMENTS[0];
        List<String> sampleTokens = tokens(sample);
        System.out.println("  Tokens de \"" + sample + "\":");
        System.out.println("  " + sampleTokens);
        System.out.println("  Reconstruction avec StringBuilder:");
        System.out.println("  " + reconstruct(sampleTokens));
        System.out.println();
        
        System.out.println("===== PARTIE B - LIST & SET =====\n");
        
        System.out.println("Q3) Filtrage des stopwords:");
        System.out.println("  Stopwords: " + stopset);
        List<String> filtered = filterStopwords(sampleTokens, stopset);
        System.out.println("  Avant filtrage: " + sampleTokens);
        System.out.println("  Après filtrage: " + filtered);
        System.out.println();
        
        System.out.println("Q4) Unicité & ordre:");
        List<String> allTokens = getAllTokens(RAW_COMMENTS, stopset);
        
        Set<String> hashSet = new HashSet<>(allTokens);
        System.out.println("  HashSet (non trié, ordre aléatoire):");
        System.out.println("  " + hashSet);
        System.out.println("  → Intérêt: Performance O(1), pas de duplication");
        System.out.println();
        
        Set<String> treeSet = new TreeSet<>(allTokens);
        System.out.println("  TreeSet (trié alphabétiquement):");
        System.out.println("  " + treeSet);
        System.out.println("  → Intérêt: Tri automatique, recherche O(log n)");
        System.out.println();
        
        Set<String> linkedSet = new LinkedHashSet<>(allTokens);
        System.out.println("  LinkedHashSet (ordre d'apparition):");
        System.out.println("  " + linkedSet);
        System.out.println("  → Intérêt: Préserve l'ordre d'insertion, pas de duplication");
        System.out.println();
        
        System.out.println("===== PARTIE C - MAP =====\n");
        
        System.out.println("Q5) Fréquences de mots:");
        Map<String, Integer> frequencies = wordFrequencies(allTokens);
        System.out.println("  Toutes les fréquences: " + frequencies);
        System.out.println();
        
        System.out.println("  Top 10 mots les plus fréquents:");
        List<Map.Entry<String, Integer>> top10 = topK(frequencies, 10);
        for (int i = 0; i < top10.size(); i++) {
            Map.Entry<String, Integer> entry = top10.get(i);
            System.out.printf("    %2d. %-15s : %d occurrences%n", 
                i + 1, entry.getKey(), entry.getValue());
        }
        System.out.println();
        
        System.out.println("Q6) Index inverse:");
        Map<String, Set<Integer>> invertedIndex = buildInvertedIndex(RAW_COMMENTS, stopset);
        
        String[] demoWords = {"java", "map", "python", "puissantes", "adore"};
        System.out.println("  Indices des commentaires contenant certains mots:");
        for (String word : demoWords) {
            Set<Integer> indices = invertedIndex.get(word);
            if (indices != null) {
                System.out.printf("    %-15s → commentaires %s%n", word, indices);
            } else {
                System.out.printf("    %-15s → (non trouvé)%n", word);
            }
        }
        System.out.println();
        System.out.println("  → TreeSet utilisé pour garantir l'ordre croissant des indices");
        
        System.out.println("Analyse terminée!");
    }
}