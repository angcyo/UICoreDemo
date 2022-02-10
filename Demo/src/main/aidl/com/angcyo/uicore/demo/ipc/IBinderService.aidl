package com.angcyo.uicore.demo.ipc;

import com.angcyo.uicore.demo.ipc.IpcData;

interface IBinderService {

    boolean addData(in IpcData data);

    List<IpcData> getData();
}
