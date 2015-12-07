/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Rafa\\workspace\\AndroidProject\\src\\com\\androidproject\\bluetooth\\IConnectionCallback.aidl
 */
package com.androidproject.bluetooth;
// Declare the interface.

public interface IConnectionCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.androidproject.bluetooth.IConnectionCallback
{
private static final java.lang.String DESCRIPTOR = "com.androidproject.bluetooth.IConnectionCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.androidproject.bluetooth.IConnectionCallback interface,
 * generating a proxy if needed.
 */
public static com.androidproject.bluetooth.IConnectionCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.androidproject.bluetooth.IConnectionCallback))) {
return ((com.androidproject.bluetooth.IConnectionCallback)iin);
}
return new com.androidproject.bluetooth.IConnectionCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_incomingConnection:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.incomingConnection(_arg0);
return true;
}
case TRANSACTION_maxConnectionsReached:
{
data.enforceInterface(DESCRIPTOR);
this.maxConnectionsReached();
return true;
}
case TRANSACTION_messageReceived:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
this.messageReceived(_arg0, _arg1);
return true;
}
case TRANSACTION_connectionLost:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.connectionLost(_arg0);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.androidproject.bluetooth.IConnectionCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void incomingConnection(java.lang.String device) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(device);
mRemote.transact(Stub.TRANSACTION_incomingConnection, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void maxConnectionsReached() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_maxConnectionsReached, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void messageReceived(java.lang.String device, java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(device);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_messageReceived, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
@Override public void connectionLost(java.lang.String device) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(device);
mRemote.transact(Stub.TRANSACTION_connectionLost, _data, null, android.os.IBinder.FLAG_ONEWAY);
}
finally {
_data.recycle();
}
}
}
static final int TRANSACTION_incomingConnection = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_maxConnectionsReached = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_messageReceived = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_connectionLost = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void incomingConnection(java.lang.String device) throws android.os.RemoteException;
public void maxConnectionsReached() throws android.os.RemoteException;
public void messageReceived(java.lang.String device, java.lang.String message) throws android.os.RemoteException;
public void connectionLost(java.lang.String device) throws android.os.RemoteException;
}
