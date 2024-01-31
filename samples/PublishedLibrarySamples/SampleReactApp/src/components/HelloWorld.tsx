import React, {FunctionComponent} from 'react';
import SharedAnalyticsLibrary from 'shared-analytics-library';
import SAL = SharedAnalyticsLibrary.dev.zawadzki.sharedanalyticslibrary
import Event = SAL.event


const HelloWorld: FunctionComponent = () => {
    const simpleEvent = new Event.SampleAdditionalButtonTapped("buttonId1");
    const eventWithLong = new Event.SampleActionWithTimer(BigInt("297153970613387264"));
    const eventWithDefaults = new Event.SampleButtonTapped("buttonId1", null, "value for someOptional2");
    const eventWithEnum = new Event.SampleSomething(true, 12, 12, 1.0, Event.SampleSomething.MyType.CUSTOM);

    SAL.executeAction();
    return (
        <div className="hello-world">
            <h1>Hello World</h1>
            <div>
                {new SAL.Greeting().greet()}
            </div>
            <br/>
            <div> simpleEvent: {simpleEvent.toString()}</div>
            <div> eventWithLong: {eventWithLong.toString()}</div>
            <div> secondEventWithDefaults: {eventWithDefaults.toString()}</div>
            <div> eventWithEnum: {eventWithEnum.toString()}</div>
        </div>
    )
};
export default HelloWorld;
