package setsuna.boardgame.utils.network;

public enum Commands{
    //Client : (Commands)CREATE_ROOM (Games)game (int)gameSize (String)hostName
    //Serveur : (Commands)CREATE_ROOM (int)roomID
    CREATE_ROOM,

    //Client : (Commands)JOIN_ROOM (int)roomId (Games) selectedGame (String)playerName
    //Serveur : (Commands)JOIN_ROOM (boolean)isJoin
    JOIN_ROOM,

    //Client : (Commands)IS_ROOM_FULL (int)roomId
    //Serveur : (Commands)IS_ROOM_FULL (boolean)isRoomFull (int)currentPlayerNumber (int)maxPlayersNumber
    IS_ROOM_FULL,

    //Client : (Commands)GET_PLAYER_NAME (int)roomId
    //Serveur : (Commands)GET_PLAYER_NAME (String)currentPlayerName
    GET_PLAYER_NAME,

    //Client : (Commands)GET_BOARD_SIZE (int)roomId
    //Serveur : (Commands)GET_BOARD_SIZE (int)boardSize
    GET_BOARD_SIZE,

    //Client : (Commands)PLAY (String)playerName (int)roomId (int)h (int)w
    //Serveur : (Commands)PLAY (boolean)isValidMove (Pawn)pawn (int)h (int)w (int)boardSize
    PLAY,

    //Client : (Commands)IS_GAME_OVER (int)roomId (String)playerName
    //Serveur : (Commands)IS_GAME_OVER (boolean)isGameOver (String)winner
    IS_GAME_OVER,

    //Client : (Commands)GIVE_UP (int)roomId (String)playerName
    //Serveur : (Commands)GIVE_UP (boolean)playerName
    GIVE_UP
}