package org.wso2.carbon.apimgt.gateway.common.jwtgenerator;

/**
 * Enum for the jwt signature algorithm
 */
public enum JWTSignatureAlg {
    SHA256_WITH_RSA("RS256"), NONE("none");

    private String jwsCompliantCode;

    JWTSignatureAlg(String s) {
        jwsCompliantCode = s;
    }

    public String getJwsCompliantCode() {
        return jwsCompliantCode;
    }
}
