package business;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAttributeMapperHelper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeycloakTokenEnhancer extends AbstractOIDCProtocolMapper implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "oidc-token-enhancer-mapper";

    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    static {
        OIDCAttributeMapperHelper.addIncludeInTokensConfig(configProperties, KeycloakTokenEnhancer.class);
    }

    private UserRepository getUserRepository() {
        try {
            String moduleName = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getName().replaceAll("\\.jar$", "");
            String jndiName = String.format("java:global/%s/%s", moduleName, UserRepository.class.getSimpleName());
            return (UserRepository) new InitialContext().lookup(jndiName);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AccessToken transformAccessToken(AccessToken accessToken, ProtocolMapperModel protocolMapperModel, KeycloakSession keycloakSession, UserSessionModel userSessionModel, ClientSessionContext clientSessionContext) {

        System.out.println("++++++++++++++++++++++++++++++++");
        UserRepository userRepository = getUserRepository();
        Object data = userRepository.getData();
        System.out.println("data: " + data);
        System.out.println("++++++++++++++++++++++++++++++++");

        accessToken.getOtherClaims().put("fruit", "pear, apple, tangerine");
        return accessToken;
    }

    @Override
    public String getDisplayCategory() {
        return "Token Enhancer";
    }

    @Override
    public String getDisplayType() {
        return "Token Enhancer";
    }

    @Override
    public String getHelpText() {
        return "Add to claims for the User Service";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    public static ProtocolMapperModel create(String name, boolean accessToken, boolean idToken, boolean userInfo) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(PROVIDER_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        Map<String, String> config = new HashMap<>();
        if (accessToken) {
            config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ACCESS_TOKEN, "true");
        }
        if (idToken) {
            config.put(OIDCAttributeMapperHelper.INCLUDE_IN_ID_TOKEN, "true");
        }
        if (userInfo) {
            config.put(OIDCAttributeMapperHelper.INCLUDE_IN_USERINFO, "true");
        }
        mapper.setConfig(config);

        return mapper;
    }


}
