package setsuna.boardgame.utils.network;

public enum Commands{
    //Client : (Commands)CREATE_ROOM (Games)game (int)gameSize (String)hostName
    //Serveur : (int)roomID
    CREATE_ROOM,

    //Client : (Commands)JOIN_ROOM (int)roomId (String)playerName
    //Serveur : (boolean)isJoin
    JOIN_ROOM,

    //TODO
    GIVE_UP,

    //Client : (Commands)IS_ROOM_FULL (int)roomId
    //Serveur : (boolean)isRoomFull (int)currentPlayerNumber (int)maxPlayersNumber
    IS_ROOM_FULL,

    //Client : (Commands)PLAY (String)playerName (int)roomId (int)h (int)w
    //Serveur : (boolean)isValidMove (Pawn)pawn
    PLAY,

    //Client : (Commands)GET_PLAYER_NAME (int)roomId
    //Serveur : (String)currentPlayerName
    GET_PLAYER_NAME,

    //Client : (Commands)GET_BOARD_SIZE (int)roomId
    //Serveur : (int)size
    GET_BOARD_SIZE,

    //Client : (Commands)IS_GAME_OVER (int)roomId
    //Serveur : (boolean)isGameOver (String)winner
    IS_GAME_OVER
}