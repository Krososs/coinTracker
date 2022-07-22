package pl.sk.coinTracker.Support;

public enum Response {
    WRONG_USERNAME,
    USERNAME_TAKEN,
    EMAIL_TAKEN,
    WRONG_PASSWORD,
    WRONG_CREDENTIALS,
    PASSWORD_TOO_SHORT,
    USER_DOES_NOT_EXISTS,
    USER_HAS_NO_RIGHTS_TO_WALLET,
    WALLET_DOES_NOT_EXISTS,
    WALLET_ALREADY_CONTAINS_COIN,
    WALLET_DOES_NOT_CONTAIN_COIN,
    WALLET_NAME_TOO_LONG,
    WRONG_WALLET_NAME,
    WRONG_WALLET_TYPE,
    WALLET_ALREADY_EXISTS,
    COIN_DOES_NOT_EXISTS,
    TRANSACTION_DOES_NOT_EXISTS,
    WRONG_TRANSACTION_AMOUNT,
    WRONG_ADDRESS,
    TRANSACTION_NOTE_TOO_LONG,
    CHAIN_DOES_NOT_EXISTS,
    CATEGORY_ALREADY_EXISTS,
    CATEGORY_DOES_NOT_EXISTS,
    USER_HAS_NO_RIGHTS_TO_CATEGORY,
    TOKEN_EXPIRED;

    public String ToString() {

        return switch (this) {
            case WRONG_USERNAME -> "Given username is incorrect";
            case USERNAME_TAKEN -> "Given username is already taken";
            case EMAIL_TAKEN -> "Given email is already in use";
            case WRONG_PASSWORD -> "Given password is incorrect";
            case WRONG_CREDENTIALS -> "Wrong credentials. Please check your username and password";
            case PASSWORD_TOO_SHORT -> "Given password is too short";
            case USER_DOES_NOT_EXISTS -> "Given user does not exists";
            case USER_HAS_NO_RIGHTS_TO_WALLET -> "User has no rights to given wallet";
            case WALLET_ALREADY_CONTAINS_COIN -> "Given wallet already contains this coin";
            case WALLET_DOES_NOT_CONTAIN_COIN -> "Given wallet does not contain this coin";
            case WALLET_DOES_NOT_EXISTS -> "Given wallet does not exist";
            case WALLET_NAME_TOO_LONG -> "Wallet name is too long";
            case WRONG_WALLET_NAME -> "Wrong wallet name";
            case WRONG_WALLET_TYPE -> "Wrong wallet type";
            case WALLET_ALREADY_EXISTS -> "User already has wallet with the given name";
            case WRONG_TRANSACTION_AMOUNT -> "Wrong transaction amount";
            case COIN_DOES_NOT_EXISTS -> "Given coin does not exist";
            case TOKEN_EXPIRED -> "Token has expired";
            case TRANSACTION_DOES_NOT_EXISTS -> "Transaction with given id does not exists";
            case WRONG_ADDRESS -> "Wrong account address";
            case CHAIN_DOES_NOT_EXISTS -> "Selected chain does not exists or is not supported yet";
            case TRANSACTION_NOTE_TOO_LONG -> "Note is too long";
            case CATEGORY_ALREADY_EXISTS -> "You already have a category with given name";
            case CATEGORY_DOES_NOT_EXISTS -> "Category does not exist";
            case USER_HAS_NO_RIGHTS_TO_CATEGORY -> "You have no rights to given category";
        };
    }
}
