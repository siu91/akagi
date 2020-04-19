package org.siu.akagi.authentication.jwt;

import javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.siu.akagi.model.JWT;

/**
 * @Author Siu
 * @Date 2020/4/19 12:18
 * @Version 0.0.1
 */
@AllArgsConstructor
@Getter
public class TokenPair {

    private Pair<String, String> t;
    private Pair<String, String> rt;

    public JWT toJWT(String username) {
        return new JWT(username, t.getValue(), rt.getValue());
    }

}
