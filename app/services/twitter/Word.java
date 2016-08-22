package services.twitter;

/**
 * Created by mahesh on 21/08/16.
 */

public class Word {
    private final String word;
    private final Long count;

    public Word(String word, Long count) {
        this.word = word;
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public String getWord() {
        return word;
    }
}
