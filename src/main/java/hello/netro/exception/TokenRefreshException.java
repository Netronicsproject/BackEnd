package hello.netro.exception;

public class TokenRefreshException extends RuntimeException {
    private String token;
    private String message;

    public TokenRefreshException(String token, String message) {
        super(String.format("리프레시 토큰 %s: %s", token, message));
        this.token = token;
        this.message = message;
    }
}