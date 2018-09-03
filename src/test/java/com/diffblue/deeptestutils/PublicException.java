package com.diffblue.deeptestutils;

public class PublicException extends Exception {

    private class PrivateInnerException extends PublicException{
    }

}
