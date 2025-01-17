package hello.netro.domain;


//스프링 시큐리티에서는 권한 코드에서 Role_이 앞에 있어야함
public enum Role {
    GUEST,  // 비회원
    USER,   // 일반 회원
    ADMIN   // 관리자
}
