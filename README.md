Assume that you have static reference  : - 


Java has 4 reference types 
1. Strong (normal)
2. Soft
3. WeakReference - Eligible for GC 
4. Phantom

Rule of the thumb is Avoid using Anonymous or Anonymous Inner class. Instead use 
Static Inner class 


Curious case of Memory Leak

We already aware Activity screen rotation every time activty get destroyed and recreated but that 
time activity is created but this object holds reference to activity not dereferenced 

Destroying exist activty 
create new activity when screen rotatio happen 
All time the object hold reference to activity not went to gc it activity having multiple launches 
leads to memory leak 

But activity expected will work but there are many hidden instance is alive , As time progressess
this will consume all memory pretty soon, app will run out of out of memory error 


The app performance degrades 
App Crashes 

How to identify memory leaks 

Finding the memory leak 

Use Android Monitor 
There are three will be there 

1. we choose memory you could see Initiate GC and Start and Stop Allocation Tracking and Dump Java 
Heap Memory 

1. We will trigger java heap dump memory 
2. HPROF (Heap/ CPU Profiling tool ) tools to analyze the heap memory dump  - In android have 
inbuild hprof 



To find Memory dump easily use LeakCanary Library to find and traceout memory leaks 

    debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.4'

If the memory is leak , LeakCanary would have triggered notification  

What is you Context ? 

Example 

someRandomSampleClass  = SomeRandomSampleClass.getSomeRandomSampleClassInstance(this); - causes memory Leak 

someRandomSampleClass  = SomeRandomSampleClass.getSomeRandomSampleClassInstance(getApplicationContext()); - They didn't makes memory leak


What is the difference this and getApplicationContext()

Why is it possible to use Activity in place of Context ?
getApplicationContext is a subclass of context 


Context is a super class and it a abstract class
                            |
`                           | 
                            |
                 ------------------------------------------------------------------------
                 |                                                                      |
     ContextWrapper is a sublcass of context in android                           ContextImpl
                             |
                            | 
     ----------------------------------------------------- ---------------------------------|------------- etc..
     |                       |                          |                                   |
   Application            Service                 ContextThemeWrapper                ContextThemeWrapper
                                                        |                                   |
                                                     Activity                           Activity 
                                                     
                                                     
Can you guess the difference b/w ContextWrapper and ContextThemeWrapper ?

    ContextWrapper is subclass of Context it will delegates all those properties to contextImpl for each instance for an activity 
    ContextThemeWrapper extends only for activity because activity rely with UI and service extends with directly 
    from context wrapper and does not contain ui


Activity / Service we are using the subclasses of ContextThemeWrapper  - comes under context 

Broadcast Receiver.onReceive(Context context ,Intent intent) - its not context however it get the 
context in the onResume method one of the argument - It is actually another specialized context 

1.ReceiverRestrictedContext - dont have the impl of bindService , registerService() method are not 
supported

@Override
public void onRecieve(Context context , Intent intent) {
Context applicationContext = context.getApplicationContext();
}

2.Context.getApplicationContext()
3.ContentProvider.getContext() - not a context 

Example 
public boolean onCreate() {
      todoListDBADapter = ToDOListDBAdapter.getToDoListDBAdapterInstance(getContext());
      return true ;
}


All this three context invokes Application class context in lifecycle above refer diagram :

Application Context - Singleton Object - It means Android only single object of application exists 


Note : - 

someRandomSampleClass  = SomeRandomSampleClass.getSomeRandomSampleClassInstance(getApplicationContext()); - They didn't makes memory leak

Everytime Activity gets created due to screen rotation - no new instance of Context are getting 
Created 



Does that mean when "this"  is used , new Context instance are getting Created ?
this contain ui contextthemewrapper for each instance for same activity . activity may have multiple instance is lead to memory leak so use getApplicationClass() provides
 singleton object 


Unrevaling Context 


ContextImpl  is the actual Context that android Implements ContextWrapper delegates all calls to 
ContextImpl

ContextWrapper is just a Adapter Design Patterns - Its Delegating all the methods to ContextImpl



Application get's own its instance of ContextImpl

getApplicationContext() returns Application 


Bear in mind - ContextWrapper is not singleton - so acitivty is (UI) related unders contextwrapper 

Application and ContextWrapper don't have contain Theme specific  info . - For Application we have only one instance 

Each Activity gets own its instance of ContextImpl - There are multiple instance for same Activity , each with their own ContextImpl \



Fact about service : - 

Remember Service is not a UI

That is why service extends ContextWrapper not ContextThemeWrapper


If a app has Service and service 2 you can run only one instance for each of those services. 
A service is started only we can do bind and unbind  any no of times .

Binding and unbinding does not create multiple instance of services .

Each Services gets own its instance of ContextImpl - per instance in a application 



Total no of Context in the application is 

         Total no of Context = #Total no of Activities + #Total no of Services + 1 Application Context 
         
Why does Activity Context Leak Memory ?


someRandomSampleClass  = SomeRandomSampleClass.getSomeRandomSampleClassInstance(this); - causes memory Leak 

  1. in protrait mode - one activity coontext will be there 
  2. In landscape mode - two activity context will be there 
  3. again protrait mode - three  context 
  etc..
  Three activity context retained  beyond on the lifecycle  3 are activity context is leaked one is 
  visible to user that why we go for getApplicationContext()
  
  
  
  So Can i use Application everywhere instead of Activity.this ?
  Answer : Is No 
  
  Where to use Which context and what are the other precautions ways to avoid Context/Memory Leak 
  
  
  
                                   Application                    Activity                     Services                    ContentProvider                  BroadCast Receiver
    
   Starting An Activity            Not Recommended                  Yes                         Not Recommended              Not Recommended                Not Recommended
       
   Layout Inflation                Not Recommended                  Yes                         Not Recommended              Not Recommended                 Not Recommended 
    
   Trigger Dialog                     No                            Yes                            No                            No                                No
   
   Starting Services                  Yes                           Yes                           Yes                            Yes                               Yes
   
   Service Binding                    Yes                           Yes                           Yes                            Yes                               No
   
   Send Broadcast                     Yes                           Yes                           Yes                            Yes                               Yes
   
   Register Broadcast                 Yes                           Yes                           Yes                            Yes                            Not Recommended 
   
   Load Resources                     Yes                           Yes                           Yes                            Yes                               Yes
      
  
  
  Using Flag 
  
  FLAG_ACTIVITY_NEW_TASK 
  
  
  Calling Activity in Service - not recommender 
  
  Trigger a service -> Notification -> Activity 
  
  Layout inflation in Service - Actually it doesn't make any sense to do any Ui Related work from service 
  Use Activity for that 