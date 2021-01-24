# About

**Que is a tool that allows for even, fair and organized distribution of cyclic tasks among the members of a group.**

This implementation of project is made as a proof of concept for one use case specifically – to 
distribute taking notes in school evenly across the members of the class. Despite that, the idea is 
more universal and so will be the final implementation of the project, which will allow it to support 
even more use cases, like household chores, sharing a single item (like computer) or sharing 
responsibilities to name a few.

The main idea behind the implementation is that the points of time in which something is happening
should be independent of the people assigned to these points in time. As a result of that, if the point
in time is drops no one misses his turn, and when the user drops the next user is assigned instead.

With this solution, each user has no interest of the state of the queue besides his turns, which allows
for swaps between users in the same queue or even between queues, which is already implemented.

Provided the proof-of-concept implementation will pass the tests and meet our expectations, we
seek to further develop and expand this idea which, we hope, will result in publicly available
universal solution.

# API
All API post responses and requests have a content Method of `application/json` and encoding UTF-8.
The format of the string date is "yyyy-mm-dd", format of the string time is "hh:mm:ss" unless specified 
otherwise.

**ALL URLS ARE PRECEDED BY A PREFIX:** `/api/v1`

## Authentication
API uses JTW based authentication, the token is required for every request except the login. It should
be sent in an `Authentication` header.

## Errors
**All errors returned by the API will follow the error scheme.**
The scheme consists of:

### Error
- `status`: `String` - The string value of error code, for example `BAD_REQUEST` for `400`
- `message`: `String`
- `suggestedAction`: `String`
- `subErrors`: `Array` - An array of [SubErrors](#SubError).
- `timestamp`: `String` representing a timestamp in format "dd:mm:yyyy hh:mm:ss" 
<a name="SubError"></a>
### SubError
- `message`: `String`
- `suggestedAction`: `String`

**An example of an error:**
```
{
  "status": "BAD_REQUEST",
  "message": "Validation failed",
  "suggestedAction": "Take look at the errors",
  "subErrors": [
    {
      "message": "Invalid value: null - Please specify old password",
      "suggestedAction": "Check the rejected value against error"
    },
    {
      "message": "Invalid value: null - Please specify new password",
      "suggestedAction": "Check the rejected value against error"
    }
  ],
  "timestamp": "17-01-2021 01:17:41"
}
```
## Login  
Returns access token if username and password are valid and account is active  
**Request:** `/auth`  
**Method:** `POST`  
**Body:**
- `username`: `String`
- `password`: `String`  

**Response:**
- `token`: `String`
- `tokenMethod`: `String` with a value of `Bearer`
## Account

### Change password

**Request:** `/account/changepass`  
**Method:** `POST`  
**Body:**
- `oldPassword`: `String`
- `newPassword`: `String` - must be size between 3 and 30 characters

**Response:** `200 OK` status or `401 Unauthorized` if supplied details are incorrect

## Subjects
<a name="SubjectEntity"></a>
### Subject Entity
- `id`: `Integer`
- `name`: `String`
- `teacher`: `String`

### Get Subjects
**Request:** `/subjects/{subject_id}`  
**Method:** `GET`  
**Response:** [SubjectEntity](#SubjectEntity)

### Get all subjects
**Request:** `/subjects`  
**Method:** `GET`  
**Response:** An array of [SubjectEntities](#SubjectEntity)
## Lessons

<a name="LessonEntity"></a>
### Lesson Entity
- `id`: `Integer`
- `lessonIndex`: `Integer` - Determines the next occurrence will be the n-th occurrence of this lesson
- `subjectId`: `Integer`
- `nextDate`: `String` - Date on which the next occurrence of this lesson should take place, in format
- `time`: `String` - A time of the next occurrence, in "hh:mm:ss" format
- `recurrenceInterval`: `Integer` - Determines every how many days should an occurrence take place

### Get lesson
**Request:** `/lessons/{lesson_id}`  
**Method:** `GET`  
**Response:** [Lesson Entity](#LessonEntity)

### Get lessons of subject
**Request:** `/subjects/{subject_id}/lessons`  
**Method:** `GET`  
**Response:** An Array of [Lesson Entities](#LessonEntity)

### Get Users in lesson
**Request:** `/lessons/{lesson_id}/users`  
**Method:** `GET`  
**Response:** An Array of `Integers` representing the IDs of users

## Occurrences
Occurrences are index, date and user bound together - basically the 
computed queue. **When requested amount of occurrences is to be specified in 
request, it can be not more than 30.**
<a name="OccurrenceEntity"></a>
### Occurrence Entity
- `lessonId`: `Integer`
- `userId`: `Integer`
- `lessonIndex`: `Integer`
- `date`: `String` 
- `time`: `String` 
- `username`: `String`

### Update occurrences
Removes old occurrences from queue and moves them to the log  
**Request:** `/occurrences/update`  
**Method:** `GET`  
**Response:** `200 OK`

### Get past occurrences
Used to retrieve the past occurrences of all lessons. The previous occurrence is an occurrence that has
both date and time set in the past.  
**Request:** `/occurrences/past/{amount}`  
**Method:** `GET`  
**Response:** An array of [Occurrence Entites](#OccurrenceEntity) with required size or less.

### Get past occurrences of lesson
**Request:** `/occurrences/past/{lessonId}/{amount}`  
**Method:** `GET`  
**Response:** An array of [Occurrence Entites](#OccurrenceEntity) with required size or less.

### Get future occurrences of lesson
**Request:** `/occurrences/next/{lessonId}/{amount}`  
**Method:** `GET`  
**Response:** An array of [Occurrence Entites](#OccurrenceEntity) with required size or less.

## Exchange requests

<a name="ExchangeRequestEntity"></a>
### Exchange request entity
- `id`: `Integer`
- `fromUserId`: `Integer`
- `fromLessonId`: `Integer`
- `fromIndex`: `Integer` - an index of the occurrence for which the sender wants to swap for
- `toUserId`: `Integer`
- `toLessonId`: `Integer`
- `toIndex`: `Integer` - an index of the occurrence the sender wants to take instead
- `status`: `String` (Enum) `PENDING` when the target user has yet undertaken no action regarding this
request, `ACCEPTED` if the target user has accepted the exchange, `DECLINED` if the target user has 
declined the exchange or `INVALID` if the target user has tried to accept the exchange, but the request
was invalid at that time. Once the status changes from `PENDING`, it can not change again.
- `resolvementTime`: `Long` - Point in time in which the target user has taken action regarding this
request that resulted in `status` change
- `fromUsername`: `String`
- `tuUsername`: `String`
- `fromDate`: `String` - Result of assigning the date to the occurrence with index "`fromIndex`", computed
at the time of responding to the request, it may change.
- `toDate`: `String` - Result of assigning the date to the occurrence with index "`toIndex`", computed
at the time of responding to the request, it may change.
- `fromTime`: `String` - Result of assigning the time to the occurrence with index "`fromIndex`", computed
  at the time of responding to the request, it may change.
- `toTime`: `String` - Result of assigning the time to the occurrence with index "`toIndex`", computed
  at the time of responding to the request, it may change.
- `fromSubjectName`: `String`
- `toSubjectName`: `String`
  
### Get requests targeted to user
**Request:** `/exchanges/requests/to`  
**Method:** `GET`  
**Response:** An array of [Exchange request entities](#ExchangeRequestEntity)

### Get request sent by user
**Request:** `/exchanges/requests/from`  
**Method:** `GET`  
**Response:** An array of [Exchange request entities](#ExchangeRequestEntity)

### Accept exchange request
**Request:** `/exchanges/requests/accept/{request_id}`  
**Method:** `GET`  
**Response:** `200 OK`, or `409 CONFLICT` if the request was invalid

### Decline exchange request
**Request:** `/exchanges/requests/decline/{request_id}`  
**Method:** `GET`  
**Response:** `200 OK`

### Submit a new exchange request
**You can submit an exchange request only up to 30 indexes ahead**  
**Request:** `/exchanges/requests/submit`  
**Method:** `POST`  
**Body:**  
- `fromUserId`: `Integer`
- `fromLessonId`: `Integer`
- `fromIndex`: `Integer` - an index of the occurrence for which the sender wants to swap for
- `toUserId`: `Integer`
- `toLessonId`: `Integer`
- `toIndex`: `Integer` - an index of the occurrence the sender wants to take instead
**Response:** `200 OK`