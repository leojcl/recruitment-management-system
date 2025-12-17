# recruitment-management-system
Using Spring boot/MVC, Thymeleaf to build a simple online job portal

Requirements - Recruiter: 
- Post new job
- View our job 
- View list of candidates that have applied for a job 
- Edit profile and upload profile photo 
Requiremnets - Canidate: 
- Can search for jobs
- Apply for a job 
- View list of jobs that has applies for 
- Edit profile and upload profile photo 
- Upload resume/CV 

Big Picture - Application architecture: 
Client -> Controller -> Service -> Repository -> DB  -> UI using Thymeleaf

* Hiểu nghiệp vụ: 
1. Có tài khoản người dùng chung để login, quản lý email, pass, status, ngày đăng ký
2. Mỗi tài khoản đó có loại tài khoản gì, nhà tuyển dụng hay người tìm việc 
3. nếu là người tìm việc thì phải có hồ sơ và skill tương ứng 
4. nếu là nhà tuyển dụng thì phải có hồ sơ nhà tuyển dụng 
** Xác định thuộc tính: 
1. Users có id, email, pass, status, ngày đăng ký, loại user 
2. loại users có id, name, danh sách user thuộc loại 
3. hồ sơ người tìm việc có id của user, user, name, city, state, country, workAuthorization, employementType, resume, profilePhoto, danh sách skill
4. hồ sơ nhà tuyển dụng: user id, user, name, city, state, country, company, profilePhoto,
5. skill có id, name, experienceLevel, yearOfExperience, hồ sơ người tìm việc 
*** xác định quan hệ: 
1. 1 loại user type sẽ có nhiều user -> onetomany
2. 1 user chỉ có 1 hồ sơ người tìm việc -> onetoone 
3. 1 user chỉ có 1 hồ sơ nhà tuyển dụng -> onetoone 
4. 1 hồ sơ người tìm việc có nhiều skill -> onetomanny 


# Check the docker version
docker --version

# Run a MySQL 8.0 container in detached mode with:
# - root password set to 'secret'
# - a database named 'tododb' created
# - container named 'mysqldb'
# - port 3306 in the container mapped to port 3307 on the host
 docker run -d -e MYSQL_ROOT_PASSWORD=recruitmentsys -e MYSQL_DATABASE=recruitment --name jobdb -p 3307:306 mysql:8.0
 
Tech stack: 

- Spring boot, Spring data JPA + Hibernate(entity mapping chuẩn, transaction, N+1, pagination)
- Spring validation, Security, JWT
- MySQL, Flyway, PostgreSQL
- Async jobs (email, parsing, indexing)
- Rate limit, caching, idempotency
- Elastic Search(search thực chiến)
- Observability: Actuator + Micrometer + Grafana + Tracing
- Redis(cache + chat session)
- Kafka(event-drivent, async processing)
- Resilience4j(reliability, timeout/retry/circuit breaker)
- Docker compose

Phase 1: Search 
**Mục tiêu:** người dùng tìm job nhanh bằng search tốt → nền để AI gọi search về sau

### Deliverables

- Full-text search (title/description/skills/company)
- Facets/filter: location, level, salary range, remote/onsite
- Ranking cơ bản 

### Tech stack 

- **Elasticsearch** 
- Spring Boot integration:
    - Dễ nhất: **Spring Data Elasticsearch** 
    - Mạnh hơn: **Elasticsearch Java API Client** 
- **Docker Compose** để chạy ES + DB local

modify location, state, country, company with a select type 
modify candidates applied -> job-details-apply 