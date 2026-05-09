package com.shopcart.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entity representing a user account in the ShopCart e-commerce system.
 *
 * <p>This entity stores user authentication and profile information required for:
 * <ul>
 *   <li>User authentication and session management</li>
 *   <li>Personal profile information</li>
 *   <li>Contact details for order processing and notifications</li>
 * </ul>
 *
 * <p><b>Business Purpose:</b> Maintains user accounts for authentication,
 * personalization, and order management in the e-commerce platform.</p>
 *
 * <p><b>Security Considerations:</b>
 * <ul>
 *   <li>Passwords are stored as hashed values (bcrypt)</li>
 *   <li>Email addresses must be unique for account identification</li>
 *   <li>All timestamps are automatically managed</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Entity
@Data
@Table(name = "users")
public class User {
    
    /**
     * Primary key identifier for the user account.
     *
     * <p>Uses UUID generation strategy to ensure globally unique identifiers
     * across all user accounts and prevent ID collisions in distributed systems.</p>
     *
     * <p><b>Business Logic:</b> UUID provides better security than sequential IDs
     * as it prevents enumeration attacks on user accounts.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Full name of the user for profile display and order processing.
     *
     * <p>Used for:
     * <ul>
     *   <li>Order shipping and billing information</li>
     *   <li>Personalized greetings and user interface</li>
     *   <li>Customer service interactions</li>
     * </ul>
     *
     * <p><b>Validation:</b> Optional field - users may provide this during profile setup.</p>
     */
    @Column(name = "full_name")
    private String fullName;

    /**
     * Email address serving as the primary user identifier and login credential.
     *
     * <p>This field is critical for:
     * <ul>
     *   <li>User authentication and login</li>
     *   <li>Password recovery and account notifications</li>
     *   <li>Order confirmations and marketing communications</li>
     * </ul>
     *
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>Must be unique across all user accounts</li>
     *   <li>Required field for account creation</li>
     *   <li>Maximum length of 255 characters for email standards compliance</li>
     * </ul>
     */
    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    /**
     * Hashed password for user authentication.
     *
     * <p>Stores the bcrypt-hashed version of the user's password for security.
     * Never stores plain text passwords.</p>
     *
     * <p><b>Security Implementation:</b>
     * <ul>
     *   <li>Uses bcrypt algorithm with automatic salt generation</li>
     *   <li>Hashing is performed in the service layer, not entity</li>
     *   <li>Password length limited to 255 characters for storage efficiency</li>
     * </ul>
     *
     * <p><b>Business Rule:</b> Required field - all users must have a password.</p>
     */
    @Column(name = "hash_password", nullable = false, length = 255)
    private String hashPassword;

    /**
     * Phone number for user contact and order processing.
     *
     * <p>Optional contact method used for:
     * <ul>
     *   <li>Order delivery coordination</li>
     *   <li>Customer service communications</li>
     *   <li>Two-factor authentication (future enhancement)</li>
     * </ul>
     *
     * <p><b>Validation:</b>
     * <ul>
     *   <li>Optional field - not required for account creation</li>
     *   <li>Maximum length of 20 characters to accommodate international formats</li>
     *   <li>Format validation handled at service layer</li>
     * </ul>
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Timestamp indicating when the user account was created.
     *
     * <p>Automatically set to current time when user object is instantiated.
     * Used for:
     * <ul>
     *   <li>User analytics and reporting</li>
     *   <li>Account age verification</li>
     *   <li>Auditing and compliance purposes</li>
     * </ul>
     *
     * <p><b>Business Logic:</b> Automatically managed - never manually set after creation.</p>
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Default constructor for User entity.
     *
     * <p>Automatically initializes the creation timestamp to ensure
     * all user accounts have a valid creation time.</p>
     *
     * <p><b>Business Rule:</b> Creation timestamp is mandatory for audit purposes.</p>
     */
    public User(){
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Parameterized constructor for creating new user accounts.
     *
     * <p>Convenience constructor that initializes all user profile information
     * while automatically setting the creation timestamp.</p>
     *
     * <p><b>Usage:</b> Typically used during user registration process
     * after password hashing has been performed.</p>
     *
     * @param fullName User's full name for profile display (optional)
     * @param email User's email address for authentication (required, unique)
     * @param hashPassword Bcrypt-hashed password for authentication (required)
     * @param phone User's phone number for contact (optional)
     */
    public User(String fullName, String email, String hashPassword, String phone){
        this();
        this.fullName = fullName;
        this.email = email;
        this.hashPassword = hashPassword;
        this.phone = phone;
    }
}