package com.tuana9a.merij;

import com.tuana9a.merij.exceptions.SortOperationNotSupported;
import org.springframework.data.domain.Sort;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortRequest {
    private String key;
    private String operator;

    public SortRequest() {
    }

    public static SortRequest resolve(String input) {
        try {
            Pattern pattern = Pattern.compile("(\\w+\\s*)(==)(.*)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (!matcher.find()) {
                return new SortRequest();
            }
            return new SortRequest()
                    .key(matcher.group(1).trim())
                    .op(matcher.group(3).trim());
        } catch (IndexOutOfBoundsException ignored) {
        }
        return new SortRequest();
    }

    public SortRequest key(String key) {
        this.key = key;
        return this;
    }

    public boolean isValid() {
        return this.key != null && this.operator != null;
    }

    public SortRequest op(String operator) {
        this.operator = operator;
        return this;
    }

    public Sort first() throws SortOperationNotSupported {
        Sort sort;
        switch (this.operator) {
            case "1":
                sort = Sort.by(Sort.Direction.ASC, this.key);
                break;
            case "-1":
                sort = Sort.by(Sort.Direction.DESC, this.key);
                break;
            default:
                throw new SortOperationNotSupported(this.operator);
        }
        return sort;
    }

    public Sort chain(Sort first) throws SortOperationNotSupported {
        Sort chain;
        switch (this.operator) {
            case "asc":
            case "1":
                chain = first.and(Sort.by(Sort.Direction.ASC, this.key));
                break;
            case "desc":
            case "-1":
                chain = first.and(Sort.by(Sort.Direction.DESC, this.key));
                break;
            default:
                throw new SortOperationNotSupported(this.operator);
        }
        return chain;
    }
}
