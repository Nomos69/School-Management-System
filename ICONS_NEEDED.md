# Drawable Icons Needed

The following icons are referenced in the app but need to be added to the drawable folders:

## Navigation and Dashboard Icons
- `ic_dashboard.xml` - Dashboard icon
- `ic_people.xml` - Users/People icon
- `ic_student.xml` - Student icon
- `ic_teacher.xml` - Teacher icon
- `ic_class.xml` - Class/Classroom icon
- `ic_attendance.xml` - Attendance/Check icon
- `ic_exam.xml` - Examination/Test icon
- `ic_money.xml` - Money/Payment icon
- `ic_library.xml` - Library/Book icon
- `ic_event.xml` - Event/Calendar icon
- `ic_message.xml` - Message/Chat icon
- `ic_settings.xml` - Settings/Gear icon
- `ic_logout.xml` - Logout/Exit icon

## Action Icons
- `ic_notifications.xml` - Notification bell icon
- `ic_person.xml` - Person/Profile icon
- `ic_lock.xml` - Lock/Security icon
- `ic_add.xml` - Add/Plus icon
- `ic_edit.xml` - Edit/Pencil icon
- `ic_delete.xml` - Delete/Trash icon
- `ic_search.xml` - Search/Magnifying glass icon
- `ic_filter.xml` - Filter icon
- `ic_save.xml` - Save/Check icon
- `ic_cancel.xml` - Cancel/Close icon

## How to Add Icons

### Option 1: Using Material Design Icons (Recommended)

1. Right-click on `res` folder in Android Studio
2. Select New → Vector Asset
3. Click on "Clip Art" icon
4. Search for the icon name (e.g., "dashboard", "person", "book")
5. Customize size and color if needed
6. Click "Next" and "Finish"

### Option 2: Download from Material Icons

1. Go to [Material Design Icons](https://fonts.google.com/icons)
2. Search for icon name
3. Download as Android XML Vector Drawable
4. Place in `res/drawable/` folder

### Option 3: Create Placeholder Icons

Create simple XML vector drawables in `res/drawable/`:

Example for `ic_notification.xml`:
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="?attr/colorControlNormal">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M12,22c1.1,0 2,-0.9 2,-2h-4c0,1.1 0.9,2 2,2zM18,16v-5c0,-3.07 -1.64,-5.64 -4.5,-6.32V4c0,-0.83 -0.67,-1.5 -1.5,-1.5s-1.5,0.67 -1.5,1.5v0.68C7.63,5.36 6,7.92 6,11v5l-2,2v1h16v-1l-2,-2z"/>
</vector>
```

## Icon Colors

Use these colors for consistency:
- Primary color: `@color/primary` (#1976D2)
- Secondary color: `@color/accent` (#FF9800)
- Gray for inactive: `@color/icon_gray` (#757575)
- White for dark backgrounds: `@color/white` (#FFFFFF)

## Icon Sizes

- Toolbar icons: 24dp × 24dp
- Dashboard icons: 48dp × 48dp
- List item icons: 40dp × 40dp
- Small icons: 18dp × 18dp

## Quick Setup Script

To quickly add placeholder icons, create these files in `res/drawable/`:

All icons should follow this template (replace the path data):

```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
  <path
      android:fillColor="@color/primary"
      android:pathData="M12,12m-10,0a10,10 0,1 1,20 0a10,10 0,1 1,-20 0"/>
</vector>
```

This creates a simple circle placeholder for each icon.

## Final Note

The app will compile and run without these icons, but they should be added for a complete user interface. Use Material Design Icons for professional appearance and consistency across Android apps.
