# summerb
Summerb is a library, it contains a set of simple but yet important building blocks needed by every enterprise web application. 

I tired of writing boiler plate code each time I start new project so I gathered standard solutions for basic tasks and created this library. As of now set building blocks contains:
* Internationalization
* Validation
* Cache helpers (TX-bound caches, JMX beans)
* Authentication & Authorization tools
* Simple email sender
* Typical implementation for C.R.U.D. operations
 * Data Access Layer (based on Spring JDBC)
 * Service Layer
 * Validation
 * Authorization
 * Spring MVC controller
 
Initial codebase is donated by me (Sergey Karpushin) from my hobby project (www.eftracker.ru). I see this as very common to any other enterprise project I'm going to build in future. So why not share it, maybe someone else will make use of it.

This project *DOES NOT* claim to be the one and the only right way to implement mentioned pieces for enterprise applications. Still I tried to design it in a way so it will be easy to substitute any part of it with your own implementation to make whole thing work.

CAUTION: This project is slowly, but constantly evolving, and as a result backward incompatibility is not maintained too much, API might change a little. 

TBD: Detailed documentation will be defined later.
